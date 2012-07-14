//
// partake.js is a JavaScript library which calls Partake API.
// partake.js depends on the latest jQuery.
//

// *** UNDER IMPLEMENTATION ***

(function() {
    /**
    * @param {!String} sessionToken
    * @returns {createPartakeClient}
    */
    function Partake(sessionToken) {
        this.sessionToken = sessionToken;
    }

    // ----------------------------------------------------------------------
    // Account

    Partake.prototype.account = {
        partake: this,

        // Gets events of account.
        getEvents: function(queryType, offset, limit) {
            var arg = {
                queryType: queryType,
                offset: offset,
                limit: limit
            };

            return $.get('/api/account/events', arg);
        },

        getTickets: function(queryType, offset, limit) {
            var arg = {
                queryType: queryType,
                offset: offset,
                limit: limit
            };

            return $.get('/api/account/tickets', arg);
        },

        getImages: function(offset, limit) {
            var arg = {
                offset: offset,
                limit: limit
            };

            return $.get('/api/account/images', arg);
        },

        getMessages: function(offset, limit) {
            var arg = {
                offset: offset,
                limit: limit
            };

            return $.get('/api/account/messages', arg);
        },

        setPreference: function(receivingTwitterMessage, profilePublic, tweetingAttendanceAutomatically) {
            var arg = {
                sessionToken: partake.sessionToken,
                receivingTwitterMessage: receivingTwitterMessage,
                profilePublic: profilePublic,
                tweetingAttendanceAutomatically: tweetingAttendanceAutomatically
            };

            return $.post('/api/account/setPreference', arg);
        },

        removeOpenID: function(identifier) {
            var arg = {
                sessionToken: partake.sessionToken,
                identifier: identifier
            };

            return $.post('/api/account/removeOpenID', arg);
        },

        revokeCalendar: function() {
            var arg = {
                sessionToken: partake.sessionToken
            };

            return $.post('/api/account/revokeCalendar', arg);
        }
    };

    // ----------------------------------------------------------------------
    // Event

    Partake.prototype.event = {
        partake: this,

        create: function(title, beginDate, endDate) {
            var arg = {
                sessionToken: partake.sessionToken,
                title: title,
                beginDate: beginDate,
                endDate: endDate
            };
            return $.post('/api/event/create', arg);
        },

        copy: function(eventId) {
            var arg = {
                sessionToken: partake.sessionToken,
                eventId: eventId
            };

            return $.post('/api/event/copy', arg);
        },

        modify: function(eventId, params) {
            var arg = {
                sessionToken: partake.sessionToken,
                eventId: eventId
            };
            for (var s in params)
                arg[s] = params[s];

            return $.post('/api/event/modify', arg);
        },

        publish: function(eventId) {
            var arg = {
                sessionToken: partake.sessionToken,
                eventId: eventId
            };

            return $.post('/api/event/publish', arg);
        },

        remove: function(eventId) {
            var arg = {
                sessionToken: partake.sessionToken,
                eventId: eventId
            };

            return $.post('/api/event/remove', arg);
        },

        search: function(query, category, sortOrder, beforeDeadlineOnly, maxNum) {
            var arg = {
                query: query,
                category: category,
                sortOrder: sortOrder,
                beforeDeadlineOnly: beforeDeadlineOnly,
                maxNum: maxNum
            };

            return $.post('/api/event/search', arg);
        },

        postComment: function(eventId, comment) {
            var arg = {
                sessionToken: partake.sessionToken,
                eventId: eventId,
                comment: comment
            };

            return $.post('/api/event/postComment', arg);
        },

        removeComment: function(commentId) {
            var arg = {
                sessionToken: partake.sessionToken,
                commentId: commentId
            };

            return $.post('/api/event/removeComment', arg);
        },

        modifyEnquete: function(eventId, ids, questions, types, options) {
            var arg = {
                sessionToken: partake.sessionToken,
                eventId: eventId,
                ids: ids,
                questions: questions,
                types: types,
                options: options
            };

            return $.post('/api/event/modifyEnquete', arg);
        },

        modifyTicket: function(eventId, tickets) {
            var arg = $.extend(tickets, {
                sessionToken: partake.sessionToken,
                eventId: eventId
            });

            return $.post('/api/event/modifyTicket', arg);
        },

        getNotifications: function(eventId) {
            var arg = {
                eventId: eventId
            };

            return $.get('/api/event/notifications', arg);
        }
    };

    // ----------------------------------------------------------------------
    // Ticket

    Partake.prototype.ticket = {
        partake: this,

        apply: function(ticketId, status, comment, enqueteAnswers) {
            var arg = {
                sessionToken: partake.sessionToken,
                ticketId: ticketId,
                status: status,
                comment: comment,
                enqueteAnswers: $.toJSON(enqueteAnswers)
            };

            return $.post('/api/ticket/apply', arg);
        },

        removeAttendant: function(userId, ticketId) {
            var arg = {
                sessionToken: partake.sessionToken,
                userId: userId,
                ticketId: ticketId
            };

            return $.post('/api/ticket/removeAttendant', arg);
        },

        changeAttendance: function(userId, ticketId, status) {
            var arg = {
                sessionToken: partake.sessionToken,
                userId: userId,
                ticketId: ticketId,
                status: status
            };

            return $.post('/api/ticket/attend', arg);
        }
    };

    // ----------------------------------------------------------------------
    // User

    Partake.prototype.user = {
        partake: this,

        getEvents: function(userId, queryType, offset, limit) {
            var arg = {
                sessionToken: partake.sessionToken,
                userId: userId,
                queryType: queryType,
                offset: offset,
                limit: limit
            };

            return $.get('/api/user/events', arg);
        },

        getTickets: function(userId, offset, limit) {
            var arg = {
                sessionToken: partake.sessionToken,
                userId: userId,
                offset: offset,
                limit: limit
            };

            return $.get('/api/user/tickets', arg);
        }
    };

    // ----------------------------------------------------------------------
    // Message

    Partake.prototype.message = {
        partake: this,

        sendMessage: function(eventId, subject, body) {
            var arg = {
                sessionToken: partake.sessionToken,
                eventId: eventId,
                subject: subject,
                body: body
            };

            return $.post('/api/event/sendMessage', arg);
        }
    };

    // ----------------------------------------------------------------------
    // Admin

    Partake.prototype.admin = {
        partake: this,

        recreateEventIndex: function() {
            var arg = {
                sessionToken: partake.sessionToken
            };
            return $.post('/api/admin/recreateEventIndex', arg);
        },

        modifySetting: function(key, value) {
            var arg = {
                sessionToken: partake.sessionToken,
                key: key,
                value: value
            };
            return $.post('/api/admin/modifySetting', arg);
        }

    };

    // ----------------------------------------------------------------------
    // Utility

    Partake.prototype.defaultFailHandler = function (xhr) {
        try {
            var json = $.parseJSON(xhr.responseText);
            var errorMessage = json.reason;

            if (json.additional) {
                for (var s in json.additional)
                    errorMessage += "\n" + s + ": " + json.additional[s];
            }

            alert(errorMessage);
        } catch (e) {
            alert('レスポンスが JSON 形式ではありません。');
        }
    };

    // ----------------------------------------------------------------------

    // expose partake client to global.
    createPartakeClient = function(sessionToken) {
        return new Partake(sessionToken);
    };
})();

exports.createPartakeClient = createPartakeClient;



