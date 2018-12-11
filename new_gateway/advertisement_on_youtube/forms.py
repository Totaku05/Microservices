from django import forms
from django.utils.safestring import mark_safe

class SignInForm(forms.Form):
	login = forms.CharField(label = mark_safe('Login<br />'), max_length = 30)
	password = forms.CharField(label = mark_safe('<br />Password<br />'), max_length = 30)
	
class SignUpForm(forms.Form):
	def add_fields(self, role):
		self.fields['role'] = forms.CharField(required = False, initial = role, widget = forms.HiddenInput())
		if(role != 'Advertiser'):
			self.fields['minPrice'] = forms.IntegerField(label = mark_safe('<br />Min price<br />'), label_suffix = '', initial = 0, min_value = 0)
			self.fields['countOfSubscribers'] = forms.IntegerField(label = mark_safe('<br />Count of subscribers<br />'), label_suffix = '', initial = 0, min_value = 0)

	firstName = forms.CharField(label = mark_safe('First name<br />'), label_suffix = '', max_length = 50)
	secondName = forms.CharField(label = mark_safe('<br />Second name<br />'), label_suffix = '', max_length = 50)
	phoneNumber = forms.CharField(label = mark_safe('<br />Phone Number<br />'), label_suffix = '', max_length = 12)
	email = forms.EmailField(label = mark_safe('<br />Email<br />'), label_suffix = '')
	login = forms.CharField(label = mark_safe('<br />Login<br />'), label_suffix = '', max_length = 30)
	password = forms.CharField(label = mark_safe('<br />Password<br />'), label_suffix = '', max_length = 30)
	card_number = forms.IntegerField(label = mark_safe('<br />Card number<br />'), label_suffix = '')

class UpdateAccountForm(forms.Form):
	sum = forms.FloatField(label = mark_safe('Sum '), label_suffix = '')
	
class UpdateStatusForm(forms.Form):
	def add_status_field(self, status):
		if(status == 'Common'):
			STATUSES = (
				('Common', 'Common'),
				('Bronze', 'Bronze'),
				('Silver', 'Silver'),
				('Gold', 'Gold'),
				('Diamond', 'Diamond'),
			)
		if(status == 'Bronze'):
			STATUSES = (
				('Bronze', 'Bronze'),
				('Silver', 'Silver'),
				('Gold', 'Gold'),
				('Diamond', 'Diamond'),
			)
		if(status == 'Silver'):
			STATUSES = (
				('Silver', 'Silver'),
				('Gold', 'Gold'),
				('Diamond', 'Diamond'),
			)
		if(status == 'Gold'):
			STATUSES = (
				('Gold', 'Gold'),
				('Diamond', 'Diamond'),
			)
		self.fields['status'] = forms.ChoiceField(label = mark_safe('<br /><br />Status'), label_suffix = '', choices = STATUSES, widget = forms.RadioSelect(), initial = status)

class VideoForm(forms.Form):
	TAGS = (
		('Food', 'Food'),
		('Cars', 'Cars'),
		('Electronics', 'Electronics'),
		('Other', 'Other'),
	)
	
	name = forms.CharField(label = mark_safe('Name<br />'), label_suffix = '', max_length = 50)
	description = forms.CharField(label = mark_safe('<br />Description<br />'), label_suffix = '', max_length = 500)
	dateOfCreation = forms.DateField(label = mark_safe('<br />Date of creation<br />'), label_suffix = '', widget = forms.SelectDateWidget())
	duration = forms.TimeField(label = mark_safe('<br />Duration<br />'), label_suffix = '', widget = forms.TimeInput(format='%H:%M:%S'))
	countOfLikes = forms.IntegerField(label = mark_safe('<br />Count of likes<br />'), label_suffix = '', initial = 0, min_value = 0)
	countOfDislikes = forms.IntegerField(label = mark_safe('<br />Count of dislikes<br />'), label_suffix = '', initial = 0, min_value = 0)
	countOfViews = forms.IntegerField(label = mark_safe('<br />Count of views<br />'), label_suffix = '', initial = 0, min_value = 0)
	tag = forms.ChoiceField(label = mark_safe('<br /><br />Tag'), label_suffix = '', choices = TAGS, widget = forms.RadioSelect())
		
class OrderForm(forms.Form):
	TAGS = (
		('Food', 'Food'),
		('Cars', 'Cars'),
		('Electronics', 'Electronics'),
		('Other', 'Other'),
	)
	
	name = forms.CharField(label = mark_safe('Name<br />'), label_suffix = '', max_length = 50)
	description = forms.CharField(label = mark_safe('<br />Description<br />'), label_suffix = '', max_length = 500)
	end_date = forms.DateField(label = mark_safe('<br />Deadline<br />'), label_suffix = '', widget = forms.SelectDateWidget())
	sum = forms.FloatField(label = mark_safe('<br />Sum<br />'), label_suffix = '')
	tag = forms.ChoiceField(label = mark_safe('<br /><br />Tag'), label_suffix = '', choices = TAGS, widget = forms.RadioSelect())