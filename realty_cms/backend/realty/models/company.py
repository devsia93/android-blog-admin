from django.db import models


class Company(models.Model):
    title = models.CharField(max_length=64, verbose_name='Название')
    phone = models.CharField(max_length=11, verbose_name='Телефон')
    address = models.CharField(max_length=128, verbose_name='Адрес')

    class Meta:
        verbose_name = 'Агенство'
        verbose_name_plural = 'Агенства'

    def __str__(self):
        return self.title
