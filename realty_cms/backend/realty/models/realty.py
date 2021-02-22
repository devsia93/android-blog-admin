from django.db import models

from realty.models.client import Client


class Realty(models.Model):
    class STATUS:
        UNKNOWN = 0
        SALE = 1
        RENT = 2

        CHOICES = (
            (UNKNOWN, 'Не определено'),
            (SALE, 'Продажа'),
            (RENT, 'Аренда'),
        )

    address = models.CharField(max_length=128, verbose_name='Адрес')
    price = models.DecimalField(max_digits=12, decimal_places=2, verbose_name='Цена')
    owner = models.ForeignKey(Client, blank=True, null=True, on_delete=models.CASCADE, related_name='realties',
                              verbose_name='Владелец')
    status = models.IntegerField(choices=STATUS.CHOICES, default=STATUS.UNKNOWN, blank=True, verbose_name='Статус')

    class Meta:
        verbose_name = 'Недвижимость'
        verbose_name_plural = 'Недвижимости'

    def __str__(self):
        return self.address
