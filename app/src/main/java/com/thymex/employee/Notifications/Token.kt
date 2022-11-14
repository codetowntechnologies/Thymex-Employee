package com.thymex.employee.Notifications

class Token {


        private var token:String=""
        protected  constructor(){}
        constructor(token: String?) {
            this.token = token!!
        }
        fun getToken():String?{
            return token
        }

        protected  fun setToken(token:String?){
            this.token =token!!
        }

}