package com.thymex.employee.Notifications

class requesruser {

    private var reqid:String = ""
    private var useremail:String= ""

    constructor()
    protected constructor(reqid: String, useremail: String) {
        this.reqid = reqid
        this.useremail = useremail


    }

    fun getreqid():String?{
        return reqid
    }
    fun setreqid(reqid:String){
        this.reqid=reqid
    }

    fun getuseremail():String?{
        return useremail
    }
    fun setuseremail(useremail:String){
        this.useremail=useremail
    }
}