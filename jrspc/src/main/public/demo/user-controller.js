
var userController = {};

function userPanelController($scope){
		
	var self = $scope;
	
	userController = self; // for outer references, like export
		
	self.user = {login: "", password: ""};
	
	self.error = "";
	self.result = "Для входа или регистрации - введите логин и пароль.";
	self.loged = false;

	
	/** This method will called at application initialization (see last string in this file). */
	
	self.trySetSessionUser = function(control){
		Server.call("testUserService", "getSessionUser", null, 
		   function(user){
			log("checkUser: user="+JSON.stringify(user));
			if(!user.id){return;}
			self.user = user;
			self.loged = true;
			self.$digest();			
		}, function(){/**on errror do nothing*/}, control);		
	}	
	
	
	/** common user methods */
	
	self.registerUser = function(control){
		Server.call("testUserService", "registerUser", self.user, 
		   function(id){
			self.user.id = id;			
			self.onSuccess("you registered with id: "+id);		
			setTimeout(function(){control.disabled = true;}, 20);
		}, self.onError, control);		
	}
	
	self.logIn = function(control){
		self.loginControl = control;
		Server.call("testUserService", "logIn", self.user, function(user){
			self.user = user;
			self.loged = true;
			self.onSuccess("you loged in with role: "+user.role);	
			setTimeout(function(){control.disabled = true;}, 20);
		}, self.onError, control);		
	}
	
	
	self.logOut = function(control){		
		Server.call("testUserService", "logOut", {}, function(){
			self.user.role = "";
			self.user.city = "";
			self.loged = false;
			self.onSuccess("you loged out");
			setTimeout(function(){
				control.disabled = true;
				if(self.loginControl){self.loginControl.disabled = false;}
			}, 20);
		}, self.onError, control);	
	}		
	
	self.getUsersCount = function(control){
		Server.call("testAdminService", "getUsersCount", null, function(count){
			self.onSuccess("users count: "+count);			
		}, self.onError, control);			
	}	
	
	self.changeCity = function(control){
		Server.call("testUserService", "changeCity", {city: self.user.city}, function(){
			self.onSuccess("users city changed to: "+self.user.city);			
		}, self.onError, control);			
	}		
	
	
	/** admin methods */
	
	self.grantRole = function(control){		
		Server.call("testAdminService", "grantRole", {role: self.role, userId: self.userId}, function(result){
     		self.onSuccess(result);		
		}, self.onError, control);		
	}	
		
	self.removeUser = function(control){
		Server.call("testAdminService", "removeUser", {userId: self.userId}, self.onSuccess, self.onError, control);		
	}		
	
	/** common callbacks */
	
	self.onError = function(error){
		self.error = error;		
		self.$digest();		
	}
	
	self.onSuccess = function(result){	
		self.result = result;
		self.error = "";
		self.$digest();		
	}		
	
	/** initialization */
	self.trySetSessionUser();
}

