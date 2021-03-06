import com.atlassian.applinks.api.ApplicationLink
import com.atlassian.applinks.api.ApplicationLinkService
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType
import com.atlassian.jira.issue.Issue
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.sal.api.net.Request
import com.atlassian.sal.api.net.Response
import com.atlassian.sal.api.net.ResponseException
import com.atlassian.sal.api.net.ResponseHandler
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder

static ApplicationLink getPrimaryConfluenceLink() {
    def applicationLinkService = ComponentLocator.getComponent(ApplicationLinkService.class)
    final ApplicationLink conflLink = applicationLinkService.getPrimaryApplicationLink(ConfluenceApplicationType.class)
    conflLink
}

// the issue provided to us in the binding - all
//Issue issue = issue

// Example
def issue = ComponentAccessor.issueManager.getIssueByCurrentKey("TESTCASE-17")
// if you don't want to create confluence pages based on some criterion like issue type, handle this, eg:
// if (issue.issueType.name != "Bug") {
//    return
//}

def confluenceLink = getPrimaryConfluenceLink()
assert confluenceLink // must have a working app link set up

def authenticatedRequestFactory = confluenceLink.createImpersonatingAuthenticatedRequestFactory()

// write storage format using an XML builder
def writer = new StringWriter()
def xml = new MarkupBuilder(writer)
xml.'ac:structured-macro'('ac:name': "jira") {
    'ac:parameter'('ac:name': "key", issue.key)
    'ac:parameter'('ac:name': "server", "System JIRA")
    'ac:parameter'('ac:name': "serverId", "YOUR-SERVER-ID")
}

// add more paragraphs etc
xml.p("Some additional info here.")

// print the storage that will be the content of the page
log.debug(writer.toString())

// set the page title - this should be unique in the space or page creation will fail
def pageTitle = issue.key + " Discussion"

def params = [
    type : "page",
    title: pageTitle,
    space: [
        key: "TEST" // set the space key - or calculate it from the project or something
    ],
    body : [
        storage: [
            value         : writer.toString(),
            representation: "storage"
        ]
    ]
]

authenticatedRequestFactory
    .createRequest(Request.MethodType.POST, "rest/api/content")
    .addHeader("Content-Type", "application/json")
    .setRequestBody(new JsonBuilder(params).toString())
    .execute(new ResponseHandler<Response>() {
        @Override
        void handle(Response response) throws ResponseException {
            if (response.statusCode != HttpURLConnection.HTTP_OK) {
                throw new Exception(response.getResponseBodyAsString())
            } else {
                def webUrl = new JsonSlurper().parseText(response.responseBodyAsString)["_links"]["webui"]
            }
        }
    })