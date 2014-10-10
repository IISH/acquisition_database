//= require jquery
//= require bootstrap
//= require bootstrap-datepicker

(function ($) {
	var attrNames = ['name', 'id'];

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
					element.attr(attrNames[i], element.attr(attrNames[i]).replace(/\[\d\]/, '[' + number + ']'));
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

	var onRowClick = function (e) {
		location.href = $(this).find('.table-click-link').text();
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

	$(document).ready(function () {
		$('.input-group.date').datepicker();

		$('button.add').click(addElement);
		$('button.remove').click(removeElement);

		$('.btn-delete').click(deleteCollection);

		$('table.table-click > tbody > tr').click(onRowClick);

		$('#onHold').change(onHoldSor);
		$('#startProcess').change(startProcessSor);
	});
})(jQuery);