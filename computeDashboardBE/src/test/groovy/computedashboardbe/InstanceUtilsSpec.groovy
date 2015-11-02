package computedashboardbe

import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.ec2.model.InstanceState
import com.amazonaws.services.ec2.model.Placement
import com.amazonaws.services.ec2.model.Reservation
import grails.test.mixin.Mock
import spock.lang.Specification

@Mock(Ec3)
class InstanceUtilsSpec extends Specification{
    private static final instanceID1 = "my-aws-instance-id-1"
    private static final instanceID2 = "my-aws-instance-id-2"
    private static final requestID = "my-request-id"
    private static final state = "stopped"
    private static final az = "us-east-1c"

    void "Test caching an instance that only has an instanceID field set"() {
        when:
        new InstanceUtils(requestID).cacheInstanceInDB(new Instance().withInstanceId(instanceID1))

        then:
        Ec3.count() == 1
        Ec3.findAllByRequestID(requestID)[0].awsID.equals(instanceID1)
    }

    void "Test caching an instance with properly filled in nested elements."() {
        when:
        def placement = new Placement().withAvailabilityZone(az)
        def newState = new InstanceState().withName(state)
        def instance = new Instance().withInstanceId(instanceID1).withPlacement(placement).withState(newState)

        new InstanceUtils(requestID).cacheInstanceInDB(instance)

        then:
        Ec3.count() == 1
        Ec3.findAllByRequestID(requestID)[0].az.equals(az)
        Ec3.findAllByRequestID(requestID)[0].state.equals(state)
    }

    void "Test caching an entire result set into the DB"() {
        when:
        def res1 = new Reservation().withInstances(new Instance().withInstanceId(instanceID1))
        def res2 = new Reservation().withInstances(new Instance().withInstanceId(instanceID2))

        new InstanceUtils(requestID).cacheInDB(new DescribeInstancesResult().withReservations(res1, res2))

        then:
        Ec3.count() == 2
    }

    void "Test pulling AWS creds from property"(){
        when:
        def creds = new InstanceUtils(requestID).getAWSCreds()
        then:
        creds.AWSAccessKeyId.equals('AKIAJDSZFEJZY7NRYJDA')
        creds.AWSSecretKey.equals('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')
    }
}
