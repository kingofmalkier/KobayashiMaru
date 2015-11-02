package computedashboard

class Ec3 {
    String awsID
    String type
    String state
    String az
    String publicIP
    String privateIP

    static constraints = {
        awsID blank:false
    }
}
