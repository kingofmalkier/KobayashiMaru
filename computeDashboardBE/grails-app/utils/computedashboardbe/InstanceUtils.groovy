package computedashboardbe

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.ec2.model.Instance
import grails.util.Holders

class InstanceUtils {
    private static grailsApplication = Holders.grailsApplication
    private String requestID

    InstanceUtils(String requestID) {
        this.requestID = requestID
    }

    public void fetchInstances() {
        def ec2Endpoints = grailsApplication.config.getProperty('elf.aws.endpoints', List.class)
        Integer batchSize = grailsApplication.config.getProperty('elf.aws.batchSize', Integer.class)
        def amazonEC2Client = new AmazonEC2Client(getAWSCreds());

        ec2Endpoints.each { endpoint ->
            amazonEC2Client.setEndpoint(endpoint);

            def req = new DescribeInstancesRequest().withMaxResults(100)
            DescribeInstancesResult result = amazonEC2Client.describeInstances(req)
            cacheInDB(result)

            while (result.nextToken) {
                result = amazonEC2Client.describeInstances(req.withNextToken(result.nextToken))
                cacheInDB(result)
            }
        }
    }

    public AWSCredentials getAWSCreds() {
        def keyID = grailsApplication.config.getProperty('elf.aws.accessKeyId')
        def key = grailsApplication.config.getProperty('elf.aws.secretKey')

        return new BasicAWSCredentials(keyID, key)
    }

    public void cacheInDB(DescribeInstancesResult result) {
        result.reservations.each { reservation ->
            reservation.instances.each { instance ->
                cacheInstanceInDB(instance)
            }
        }
    }

    public void cacheInstanceInDB(Instance ec2Instance) {
        Ec3 instance = new Ec3()

        instance.awsID = ec2Instance.instanceId
        instance.publicIP = ec2Instance.publicIpAddress ?: ''
        instance.privateIP = ec2Instance.privateIpAddress ?: ''
        instance.type = ec2Instance.instanceType ?: ''
        instance.state = ec2Instance.state ? ec2Instance.state.name ?: '' : ''
        instance.az = ec2Instance.placement ? ec2Instance.placement.availabilityZone ?: '' : ''
        instance.requestID = requestID

        instance.save(failOnError: true)
    }

    String getRequestID() {
        return requestID
    }

    void setRequestID(String requestID) {
        this.requestID = requestID
    }
}
