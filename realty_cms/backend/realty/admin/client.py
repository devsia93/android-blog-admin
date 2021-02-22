from django.contrib import admin

from realty.models import Client, Realty


class RealtyInline(admin.TabularInline):
    model = Realty
    fields = ('address', 'price', 'status')
    extra = 0


@admin.register(Client)
class ClientAdmin(admin.ModelAdmin):
    inlines = (RealtyInline,)
    list_display = ('fio',
                    'age',
                    'phone',
                    'status')
    list_filter = ('status',)
    search_fields = ('fio', 'age', 'phone')
