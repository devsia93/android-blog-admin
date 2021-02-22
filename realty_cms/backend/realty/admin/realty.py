from django.contrib import admin

from realty.models import Realty


@admin.register(Realty)
class RealtyAdmin(admin.ModelAdmin):
    list_display = ('address', 'price', 'owner', 'status')
    search_fields = ('address', 'price', 'owner')
    list_filter = ('status',)
