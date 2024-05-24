document.addEventListener('DOMContentLoaded', function () {
    var calendarEl = document.getElementById('calendar');
    var calendarColors = {
        1: '#008054',
        2: '#c536e7',
        3: '#019dbe',
        4: '#919396',
        9: '#4ca22e'
    };
    var calendar = new FullCalendar.Calendar(calendarEl, {
        timeZone: 'UTC+3',
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'month,agendaWeek,agendaDay'
        },
        businessHours: {
            start: '10:00',
            end: '18:00',
            dow: [1, 2, 3, 4, 5]
        },
        views: {
            month: {
                type: 'dayGridMonth',
                buttonText: 'Месяц'
            },
            agendaWeek: {
                type: 'timeGrid',
                duration: { days: 5 },
                buttonText: 'Неделя'
            },
            agendaDay: {
                type: 'timeGrid',
                duration: { days: 1 },
                buttonText: 'День'
            }
        },
        timeFormat: 'H:mm',
        weekends: false,
        weekNumbers: false,
        height: 'auto',
        themeSystem: 'bootstrap5',
        events: eventsList,
        eventClick: function (info) {
            // Установить данные модального окна
            document.getElementById('eventModalLabel').textContent = info.event.title;
            document.getElementById('eventDate').textContent = new Date(info.event.start).toLocaleString('ru-RU', {
                day: 'numeric',
                month: 'long',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
            document.getElementById('eventContent').innerHTML = info.event.extendedProps.content; // Используем innerHTML

            // Показать модальное окно
            var eventModal = new bootstrap.Modal(document.getElementById('eventModal'), {
                keyboard: false
            });
            eventModal.show();
        }
    });
    calendar.setOption('locale', 'ru');
    calendar.render();

    // Применение стилей к кнопкам после рендеринга календаря
    var buttons = document.querySelectorAll('.fc .fc-button');
    buttons.forEach(function (button) {
        button.style.backgroundColor = calendarColors[1];
        button.style.color = '#FFFFFF';
    });
});