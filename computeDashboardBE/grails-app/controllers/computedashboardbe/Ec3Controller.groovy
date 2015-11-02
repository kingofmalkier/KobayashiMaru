package computedashboardbe

import grails.converters.JSON
import grails.transaction.Transactional

@Transactional(readOnly = true)
class Ec3Controller {
    static allowedMethods = [save: "POST", delete: "DELETE"]

    private InstanceUtils instanceUtils

    def index(Integer max) {
        String requestID = params.requestID
        if(!requestID) {
            response.status = 400
            return render([reason: 'You must supply the requestID parameter.'] as JSON)
        }

        try {
            params.max = Math.min(max ?: 10, 100)

            if (params.refresh) {
                Ec3.findAllWhere(requestID: requestID).each { it.delete(flush: true, failOnError: true) }
            }

            if (Ec3.countByRequestID(requestID) == 0) {
                getInstanceUtils(requestID).fetchInstances()
            }
            render(contentType: 'text/json') {
                [
                        'instanceCount': Ec3.countByRequestID(requestID),
                        'instances'    : Ec3.findAllByRequestID(requestID, params)
                ]
            }
        } catch (Exception e) {
            println e.getMessage()
            response.status = 500
            return render([reason: 'An error occurred while fetching fresh EC2 instance data from AWS. Please contact the administrator.'] as JSON)
        }
    }

    InstanceUtils getInstanceUtils(String requestID) {
        if(instanceUtils) {
            instanceUtils.setRequestID(requestID)
        } else {
            instanceUtils = new InstanceUtils(requestID)
        }

        return instanceUtils
    }

    void setInstanceUtils(InstanceUtils instanceUtils) {
        this.instanceUtils = instanceUtils
    }
}
