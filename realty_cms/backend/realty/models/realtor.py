from django.db import models

from realty.models.company import Company


class Realtor(models.Model):
    company = models.ForeignKey(Company, on_delete=models.CASCADE, blank=True, null=True, related_name='realtors',
                                verbose_name='Агенство')
    fio = models.CharField(max_length=128, verbose_name='ФИО')
    phone = models.CharField(max_length=11, verbose_name='Телефон')

    class Meta:
        verbose_name = 'Риелтор'
        verbose_name_plural = 'Риелторы'

    def __str__(self):
        return f'{self.fio} ({self.company})'
