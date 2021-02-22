from django.db import models


class Client(models.Model):
    class STATUS:
        UNKNOWN = 0
        SELLER = 1
        CUSTOMER = 2
        SELLER_AND_CUSTOMER = 3

        CHOICES = (
            (UNKNOWN, 'Не определено'),
            (SELLER, 'Продает'),
            (CUSTOMER, 'Покупает'),
            (SELLER_AND_CUSTOMER, 'Продает/покупает'),
        )

    fio = models.CharField(max_length=128, verbose_name='ФИО')
    age = models.IntegerField(blank=True, null=True, default=None, verbose_name='Возраст')
    phone = models.CharField(max_length=11, verbose_name='Телефон')
    # TODO: ForeignKey to district
    status = models.IntegerField(default=0, choices=STATUS.CHOICES, verbose_name='Статус')

    class Meta:
        verbose_name = 'Клиент'
        verbose_name_plural = 'Клиенты'

    def __str__(self):
        return f'{self.fio}'
