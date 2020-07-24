import com.atlassian.jira.component.ComponentAccessor

// The user name of the user you want to store user properties in
final userName = "rolitvinov"
// The key of the user property to set
final userPropertyKey = "jira.meta.phone"


// The value of the user property
final userPropertyValue = "data"

def userPropertyManager = ComponentAccessor.userPropertyManager
def user = ComponentAccessor.userManager.getUserByName(userName)

assert user : "Could not find user with user name $userName"

// Set the user property
userPropertyManager.getPropertySet(user).setString(userPropertyKey, userPropertyValue)

// Get the user property
userPropertyManager.getPropertySet(user).getString(userPropertyKey)