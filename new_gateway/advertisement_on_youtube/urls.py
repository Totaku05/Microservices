from django.conf.urls import url

from . import views

urlpatterns = [
	url(r'^$', views.sign_in),
	url(r'^sign_up/$', views.sign_up),
	url(r'^change_user_info/$', views.change_user_info),
	url(r'^update_account/$', views.update_account),
	url(r'^update_status/$', views.update_status),
	url(r'^new_video/$', views.new_video),
	url(r'^videos/$', views.videos),
	url(r'^update_video/$', views.update_video),
	url(r'^orders/$', views.orders),
	url(r'^new_order/$', views.new_order),
]