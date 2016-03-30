//= require jquery
//= require bootstrap
//= require bootstrap-datepicker

(function ($) {
    var attrNames = ['name', 'id'];

    var checkHasManyFields = function () {
        $('.elements .removables').each(function (index, elem) {
            if ($(elem).find('.removable').length === 0) {
                addElement({target: elem});
            }
        });
    };

    var addElement = function (e) {
        var container = $(e.target).parents('.elements');
        var clonedElement = container.find('.removable.hidden').clone(true);
        var clonedFormElements = clonedElement.find('input, select, textarea');
        var nextNumber = container.find('.next-number');

        clonedFormElements.each(function () {
            for (var i = 0; i < attrNames.length; i++) {
                var element = $(this);
                element.attr(attrNames[i], element.attr(attrNames[i]).replace('[]', '[' + nextNumber.val() + ']'));
            }
        });

        nextNumber.val(parseInt(nextNumber.val()) + 1);
        container.find('.removables').append(clonedElement);
        clonedElement.removeClass('hidden');
    };

    var removeElement = function (e) {
        var container = $(e.target).parents('.elements');
        $(e.target).parents('.removable').remove();

        container.find('.removables .removable').each(function (number, removable) {
            $(removable).find('input, select, textarea').each(function () {
                var element = $(this);
                for (var i = 0; i < attrNames.length; i++) {
                    element.attr(attrNames[i], element.attr(attrNames[i]).replace(/\[\d+\]/, '[' + number + ']'));
                }
            });
        });

        var nextNumber = container.find('.next-number');
        nextNumber.val(parseInt(nextNumber.val()) - 1);
    };

    var deleteCollection = function (e) {
        var confirmDelete = confirm('Are you sure you want to delete this collection?');
        if (!confirmDelete) {
            e.preventDefault();
        }
    };

    var confirmation = function (e) {
        var confirmed = confirm('Are you sure?');
        if (!confirmed) {
            e.preventDefault();
        }
    };

    var deletePhoto = function (e) {
        var deletedPhotosElem = $('#deletedPhotos');
        var deletedPhotos = deletedPhotosElem.val().split(';');
        if (deletedPhotos[0] === '') {
            deletedPhotos.shift();
        }

        var deletedPhotoElem = $(e.target).parents('li');
        var deletedPhoto = deletedPhotoElem.find('input[name="collection.uploadedPhoto.id"]').val();
        deletedPhotoElem.remove();

        deletedPhotos.push(deletedPhoto);
        deletedPhotosElem.val(deletedPhotos.join(';'));
    };

    var onRowClick = function (e) {
        location.href = $(this).find('.table-click-link').text();
    };

    var onColumnCheckboxClick = function (e) {
        if (e.target.type !== 'checkbox') {
            $(':checkbox', this).trigger('click');
        }
    };

    var onCheckboxCheckAll = function (e) {
        var checkbox = $(e.target);
        var isChecked = checkbox.is(':checked');

        checkbox.closest('tbody, .modal').find('input[type=\'checkbox\']').each(function () {
            $(this).prop('checked', isChecked).trigger('change');
        });
    };

    var onCheckboxCheckWithClass = function (e, className) {
        $(e.target).closest('tbody, .modal').find('input[type=\'checkbox\']').each(function () {
            var self = $(this);
            self.prop('checked', self.hasClass(className)).trigger('change');
        });
    };

    var onButtonChange = function (e) {
        var self = $(this);
        var isChecked = self.is(':checked');

        if (isChecked) {
            self.closest('.btn').addClass('active');

            $('[name=\'' + self.attr('name') + '\']').not(self).each(function () {
                $(this).prop('checked', false).closest('.btn').removeClass('active');
            });
        }
        else {
            self.closest('.btn').removeClass('active');
        }
    };

    var onDecimalField = function (e) {
        // Don't allow a dot (key code = 46)
        if (e.which === 46) {
            return false;
        }
    };

    var onIntegerField = function (e) {
        // Don't allow a dot (key code = 46) or a comma (key code = 44)
        if (e.which === 46 || e.which === 44) {
            return false;
        }
    };

    var onFormElement = function (e) {
        // Disable the enter key, unless the focused element is a button
        if ((e.which === 13) && !$(e.target).is('button')) {
            e.preventDefault();
        }
    };

    $(document).ready(function () {
        checkHasManyFields();

        $('.input-group.date').datepicker();

        $('button.add').click(addElement);
        $('button.remove').click(removeElement);
        $('button.remove-image').click(deletePhoto);

        $('.btn-delete').click(deleteCollection);
        $('.confirm').click(confirmation);

        $('table.table-click > tbody > tr').click(onRowClick);
        $('table.checkbox-click > tbody td').click(onColumnCheckboxClick);
        $('table .checkAll').change(onCheckboxCheckAll);

        $('label.btn > input[type=\'checkbox\']').change(onButtonChange);

        $('.decimal').keypress(onDecimalField);
        $('.integer').keypress(onIntegerField);

        $(document).on('keydown', 'form input', onFormElement);

        var exportModal = $('#exportModal');
        exportModal.find('form').submit(function (e) {
            exportModal.modal('hide');
        });

        var exportButtonChanging = false;

        var exportRadioAll = exportModal.find('input[type=\'radio\'].all');
        var exportRadioAllLabel = exportRadioAll.closest('label');
        exportRadioAll.change(function (e) {
            exportButtonChanging = true;
            if (exportRadioAll.is(':checked')) {
                onCheckboxCheckAll(e);
            }
            exportButtonChanging = false;
        });

        var exportRadioDefault = exportModal.find('input[type=\'radio\'].default');
        var exportRadioDefaultLabel = exportRadioDefault.closest('label');
        exportRadioDefault.change(function (e) {
            exportButtonChanging = true;
            if (exportRadioDefault.is(':checked')) {
                onCheckboxCheckWithClass(e, 'default');
            }
            exportButtonChanging = false;
        });

        var exportColumns = exportModal.find('input[type=\'checkbox\']');
        exportColumns.change(function (e) {
            var allChecked = true;
            var defaultChecked = true;

            exportColumns.each(function () {
                var self = $(this);
                if (self.is(':checked')) {
                    if (!self.hasClass('default')) {
                        defaultChecked = false;
                    }
                }
                else {
                    allChecked = false;
                    if (self.hasClass('default')) {
                        defaultChecked = false;
                    }
                }
            });

            if (!exportButtonChanging) {
                exportRadioAll.prop('checked', allChecked);
                allChecked
                    ? exportRadioAllLabel.addClass('active')
                    : exportRadioAllLabel.removeClass('active');

                exportRadioDefault.prop('checked', defaultChecked);
                defaultChecked
                    ? exportRadioDefaultLabel.addClass('active')
                    : exportRadioDefaultLabel.removeClass('active');
            }
        });
    });

    // Keep session alive, call every 15 minutes
    setInterval(function () {
        $.get('session/keepalive');
    }, 15 * 60 * 1000);
})(jQuery);