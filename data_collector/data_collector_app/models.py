from django.db import models

class Blogger(models.Model):
	id = models.CharField(primary_key = True, max_length = 50)
	title = models.CharField(max_length = 200)
	description = models.CharField(max_length = 10000)
	publishedAt = models.DateTimeField('publishedAt')
	subscriberCount = models.PositiveIntegerField()
	videoCount = models.PositiveIntegerField()
	def __str__(self):
		return self.id + ' ' + self.title + ' ' + str(self.publishedAt) + ' ' + \
			str(self.subscriberCount) + ' ' + str(self.videoCount)

class Video(models.Model):
	id = models.CharField(primary_key = True, max_length = 50)
	blogger = models.ForeignKey(Blogger, on_delete = models.CASCADE)
	title = models.CharField(max_length = 200)
	description = models.CharField(max_length = 15000)
	publishedAt = models.DateTimeField('publishedAt')
	duration = models.CharField(max_length = 20)
	viewCount = models.PositiveIntegerField()
	likeCount = models.PositiveIntegerField()
	dislikeCount = models.PositiveIntegerField()
	category = models.CharField(max_length = 50)
	completedOrder = models.CharField(max_length = 50, default = None, null = True)
	def __str__(self):
		return self.id + ' ' + self.blogger.id + ' ' + str(self.publishedAt) + ' ' + \
			self.duration + ' ' + str(self.viewCount) + ' ' + str(self.likeCount) + ' ' + \
			str(self.dislikeCount) + ' ' + self.category