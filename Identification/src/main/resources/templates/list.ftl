<div class="generic-container">
    <div class="panel panel-default">
        <!-- Default panel contents -->
        <div class="panel-heading"><span class="lead">User </span></div>
		<div class="panel-body">
	        <div class="formcontainer">
	            <div class="alert alert-success" role="alert" ng-if="ctrl.successMessage">{{ctrl.successMessage}}</div>
	            <div class="alert alert-danger" role="alert" ng-if="ctrl.errorMessage">{{ctrl.errorMessage}}</div>
	            <form ng-submit="ctrl.submit()" name="myForm" class="form-horizontal">
	                <input type="hidden" ng-model="ctrl.user.id" />
	                <div class="row">
	                    <div class="form-group col-md-12">
	                        <label class="col-md-2 control-lable" for="login">Login</label>
	                        <div class="col-md-7">
	                            <input type="text" ng-model="ctrl.user.login" id="login" class="username form-control input-sm" placeholder="Enter your login" required ng-minlength="3"/>
	                        </div>
	                    </div>
	                </div>

	                <div class="row">
	                    <div class="form-group col-md-12">
	                        <label class="col-md-2 control-lable" for="password">Password</label>
	                        <div class="col-md-7">
	                            <input type="text" ng-model="ctrl.user.password" id="password" class="form-control input-sm" placeholder="Enter your password" required ng-minlength="3"/>
	                        </div>
	                    </div>
	                </div>
	
	                <div class="row">
	                    <div class="form-group col-md-12">
	                        <label class="col-md-2 control-lable" for="role">Role</label>
	                        <div class="col-md-7">
                                <select ng-model="ctrl.user.role" id="role" placeholder="Select your role">
                                    <option value="Blogger">Blogger</option>
                                    <option value="Advertiser">Advertiser</option>
                                </select>
	                        </div>
	                    </div>
	                </div>

                    <div class="row">
                        <div class="form-group col-md-12">
                            <label class="col-md-2 control-lable" for="firstName">First Name</label>
                            <div class="col-md-7">
                                <input type="text" ng-model="ctrl.user.contactInfo.firstName" id="firstName" class="form-control input-sm" placeholder="Enter your first name" required ng-minlength="3"/>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="form-group col-md-12">
                            <label class="col-md-2 control-lable" for="secondName">Second Name</label>
                            <div class="col-md-7">
                                <input type="text" ng-model="ctrl.user.contactInfo.secondName" id="secondName" class="form-control input-sm" placeholder="Enter your second name" required ng-minlength="3"/>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="form-group col-md-12">
                            <label class="col-md-2 control-lable" for="phoneNumber">Phone Number</label>
                            <div class="col-md-7">
                                <input type="text" ng-model="ctrl.user.contactInfo.phoneNumber" id="phoneNumber" class="form-control input-sm" placeholder="Enter your phone number" required ng-minlength="3"/>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="form-group col-md-12">
                            <label class="col-md-2 control-lable" for="email">Email</label>
                            <div class="col-md-7">
                                <input type="text" ng-model="ctrl.user.contactInfo.email" id="email" class="form-control input-sm" placeholder="Enter your email" required ng-minlength="3"/>
                            </div>
                        </div>
                    </div>

	                <div class="row">
	                    <div class="form-actions floatRight">
	                        <input type="submit"  value="{{!ctrl.user.id ? 'Add' : 'Update'}}" class="btn btn-primary btn-sm" ng-disabled="myForm.$invalid || myForm.$pristine">
	                        <button type="button" ng-click="ctrl.reset()" class="btn btn-warning btn-sm" ng-disabled="myForm.$pristine">Reset Form</button>
	                    </div>
	                </div>
	            </form>
    	    </div>
		</div>	
    </div>
    <div class="panel panel-default">
        <!-- Default panel contents -->
        <div class="panel-heading"><span class="lead">List of Users </span></div>
		<div class="panel-body">
			<div class="table-responsive">
		        <table class="table table-hover">
		            <thead>
		            <tr>
		                <th>id</th>
		                <th>password</th>
		                <th>role</th>
		                <th>login</th>
                        <th>firstName</th>
                        <th>secondName</th>
                        <th>phoneNumber</th>
                        <th>email</th>
		                <th width="100"></th>
		                <th width="100"></th>
		            </tr>
		            </thead>
		            <tbody>
		            <tr ng-repeat="u in ctrl.getAllUsers()">
		                <td>{{u.id}}</td>
		                <td>{{u.password}}</td>
		                <td>{{u.role}}</td>
		                <td>{{u.login}}</td>
                        <td>{{u.contactInfo.firstName}}</td>
                        <td>{{u.contactInfo.secondName}}</td>
                        <td>{{u.contactInfo.phoneNumber}}</td>
                        <td>{{u.contactInfo.email}}</td>
		                <td><button type="button" ng-click="ctrl.editUser(u.id)" class="btn btn-success custom-width">Edit</button></td>
		                <td><button type="button" ng-click="ctrl.removeUser(u.id)" class="btn btn-danger custom-width">Remove</button></td>
		            </tr>
		            </tbody>
		        </table>		
			</div>
		</div>
    </div>
</div>