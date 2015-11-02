package computedashboard

import groovyx.net.http.RESTClient

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Transactional(readOnly = true)
class Ec3Controller {
    def springSecurityService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @Secured('ROLE_ADMIN')
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        try {
            def results = getListOfInstances()
            respond results.instances, model: [ec3Count: results.count]
        } catch (Exception e) {
            flash.error = "elf.error.instances.lookup"
            redirect(action: 'failure')
        }
    }

    @Secured('ROLE_ADMIN')
    def failure() {
        if(!flash.error) {
            redirect(action: 'index')
        }
    }

    def getListOfInstances() {
        def auth = springSecurityService.currentUser
        String username = auth.username

        def instances = []
        def backendIP = grailsApplication.config.getProperty('elf.aws.backendIP')

        def client = new RESTClient("http://${backendIP}:9001/")

        params.put('requestID', username)
        def resp = client.get(path : 'instances', query: params)

        resp.data.instances.each {
            Ec3 instance = new Ec3()
            instance.awsID = it.awsID
            instance.type = it.type
            instance.state = it.state
            instance.az = it.az
            instance.privateIP = it.privateIP
            instance.publicIP = it.publicIP
            instances.add(instance)
        }

        return [instances: instances, count: resp.data.instanceCount]
    }
}
