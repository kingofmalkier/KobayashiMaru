import computedashboard.Role
import computedashboard.User
import computedashboard.UserRole

class BootStrap {

    def init = { servletContext ->
        def adminRole = new Role('ROLE_ADMIN').save()

        def testUser = new User('me', 'password').save()

        UserRole.create testUser, adminRole, true

        assert User.count() == 1
        assert Role.count() == 1
        assert UserRole.count() == 1
    }
    def destroy = {
    }
}
