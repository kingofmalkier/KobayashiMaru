package computedashboard

import grails.test.mixin.integration.Integration
import grails.transaction.*

import spock.lang.*
import geb.spock.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Integration
class DashboardFunctionalSpec extends GebSpec {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        when:"The home page is visited"
            go '/'

        then:"The we should see the login screen"
        	$('title').text() == "Login"

        when:"The credentials are entered"
            $('#username').value('me')
            $('#password').value('password')
            $('#submit').click()

        then:"We should see the main page"
            $('title').text() == "ELF EC2 Instances"

        //Hyper-basic confirmation that app is up, running, and can contact the backend
        //otherwise we'd be seeing different title text. A lot more could be done here, but
        //modeling framework-generated pages in geb is...fun...and I simply ran out of time
    }
}
