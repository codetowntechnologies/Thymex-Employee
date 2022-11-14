package com.thymex.employee

class users {
    private var phone: String = ""
    private var share: String = ""
    private var latitudes: String = ""
    private var type: String = ""
    private var name: String = ""
private var longitudes:String=""
    private var uid:String=""

    constructor()
    protected constructor(phone: String, share: String, latitudes: String,longitudes:String,uid:String,type:String, name:String) {
        this.phone = phone
        this.share = share
        this.latitudes = latitudes
        this.longitudes=longitudes
        this.uid=uid
        this.type=type
        this.name=name
    }
    fun getuid():String?{
        return uid
    }
    fun setuid(uid:String){
        this.uid=uid
    }



    fun getlongitudes():String?{
        return longitudes
    }
    fun setlongitudes(longitudes:String){
        this.longitudes=longitudes
    }

    fun getshare():String?{
        return share
    }
    fun setshare(share:String){
        this.share=share
    }


    fun getphone():String?{
        return phone
    }
    fun setphone(phone:String){
        this.phone=phone
    }






    fun getlatitudes():String?{
        return latitudes
    }
    fun setlatitudes(latitudes:String){
        this.latitudes=latitudes
    }


    fun gettype():String?{
        return type
    }
    fun settype(type:String){
        this.type=type
    }



    fun getname():String?{
        return name
    }
    fun setname(name:String){
        this.name=name
    }


}