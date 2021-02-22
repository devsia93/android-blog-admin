from django.contrib import admin

from realty.models import Realtor


@admin.register(Realtor)
class RealtorAdmin(admin.ModelAdmin):
    list_display = ('fio', 'phone', 'company')
    search_fields = ('fio', 'phone', 'company')
