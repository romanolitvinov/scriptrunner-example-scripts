import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser

def issue = event.issue as MutableIssue

final customFieldManager = ComponentAccessor.customFieldManager
final statusName = 'Testing'

def roleName = (issue.status.name == statusName) ? 'Tester' : 'Engineer'
def assignee = issue.getCustomFieldValue(customFieldManager.getCustomFieldObjects(issue).find { it.name == roleName }) as ApplicationUser

if (!assignee) {
    return
}

if (issue.assignee && issue.assignee.username == assignee.username) {
    return
}

def currentUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
issue.setAssignee(assignee)
ComponentAccessor.issueManager.updateIssue(currentUser, issue, EventDispatchOption.ISSUE_UPDATED, false)

