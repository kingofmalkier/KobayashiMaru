package computedashboardbe

import grails.test.mixin.*
import spock.lang.*

@TestFor(Ec3Controller)
@Mock(Ec3)
class Ec3ControllerSpec extends Specification {

    private static final instanceID1 = "my-aws-instance-id-1"
    private static final requestID = "my-request-id"
    private static final instance = new Ec3(
            awsID: instanceID1,
            requestID: requestID,
            state: 'running',
            publicIP: '1.2.3.4',
            privateIP: '10.20.30.40',
            type: 'micro',
            az: 'us-left-1b'
    )

    def populateValidParams(params) {
        assert params != null

        params["requestID"] = requestID
    }

    void "Test the index action returns the correct model for one pre-populated instance"() {
        when:"We populate one instance"
            instance.save(flush: true, failOnError: true)

        then:"We have one instance."
            Ec3.count() == 1

        when:"We start with an instance already populated and call the index action"
            //instance.save(flush: true)
            populateValidParams(params)
            controller.index()

        then:"We get back our instance without any new ones being created"
            response.status == 200
            response.json.instances[0].awsID.equals(instanceID1)
            Ec3.count() == 1
    }

    void "Test the index action will fetch instances when empty"() {
        def InstanceUtils instanceUtilsMock = Mock()

        when:
            instanceUtilsMock.setRequestID(requestID)
            populateValidParams(params)
            controller.setInstanceUtils(instanceUtilsMock)
            controller.index()

        then:
            1 * instanceUtilsMock.fetchInstances()
    }

    void "Test the index action will fetch instances when asked to refresh even with one instance"() {
        def InstanceUtils instanceUtilsMock = Mock()

        when:"We populate one instance"
            instance.save(flush: true, failOnError: true)

        then:"We have one instance."
            Ec3.count() == 1

        when:"We call the index action with refresh=true"
            instanceUtilsMock.setRequestID(requestID)
            populateValidParams(params)
            params.refresh = "true"
            controller.setInstanceUtils(instanceUtilsMock)
            controller.index()

        then:
            1 * instanceUtilsMock.fetchInstances()
    }

    void "Test the index action return a 400 if no requestID is specified"() {
        when:"The index action is executed without requestID"
            controller.index()

        then:"We get a 400 error"
            response.status == 400
    }
}
