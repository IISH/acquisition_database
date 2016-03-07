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

    var onRowCheckboxClick = function (e) {
        if (e.target.type !== 'checkbox') {
            $(':checkbox', this).trigger('click');
        }
    };

    var onCheckboxChange = function (e) {
        var checkbox = $(this);

        if (checkbox.is(':checked')) {
            checkbox.parents('tr').addClass('info');
        }
        else {
            checkbox.parents('tr').removeClass('info');
        }
    };

    var onCheckboxCheckAll = function (e) {
        var checkbox = $(this);
        var isChecked = checkbox.is(':checked');

        checkbox.parents('table').find('tbody input[type=\'checkbox\']').each(function () {
            $(this).prop('checked', isChecked).trigger('change');
        });
    };

    var onHoldSor = function (e) {
        if ($(this).is(':checked')) {
            $('#startProcess').attr('checked', false);
        }
    };

    var startProcessSor = function (e) {
        if ($(this).is(':checked')) {
            $('#onHold').attr('checked', false);
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

    $(document).ready(function () {
        checkHasManyFields();

        $('.input-group.date').datepicker();

        $('button.add').click(addElement);
        $('button.remove').click(removeElement);
        $('button.remove-image').click(deletePhoto);

        $('.btn-delete').click(deleteCollection);
        $('.confirm').click(confirmation);

        $('table.table-click > tbody > tr').click(onRowClick);
        $('table.checkbox-click > tbody > tr').click(onRowCheckboxClick);
        $('table.checkbox-click > tbody input[type=\'checkbox\']').change(onCheckboxChange);
        $('table .checkAll').change(onCheckboxCheckAll);

        $('#onHold').change(onHoldSor);
        $('#startProcess').change(startProcessSor);

        $('.decimal').keypress(onDecimalField);
        $('.integer').keypress(onIntegerField);
    });

    // Keep session alive, call every 15 minutes
    setInterval(function () {
        $.get('session/keepalive');
    }, 15 * 60 * 1000);
})(jQuery);