from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.index),
	url(r'^authorize/$', views.authorize),
	url(r'^oauth2callback/$', views.oauth2callback),
]