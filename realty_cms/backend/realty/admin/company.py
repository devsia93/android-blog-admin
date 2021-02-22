from django.contrib import admin

from realty.models import Company, Realtor


class RealtorInline(admin.TabularInline):
    model = Realtor
    fields = ('fio', 'phone')
    extra = 0


@admin.register(Company)
class CompanyAdmin(admin.ModelAdmin):
    inlines = (RealtorInline,)
    list_display = ('title', 'phone', 'address')
    search_fields = ('title', 'address', 'phone')
