package org.iish.acquisition.controller

/**
 * Simple controller to make sure the session does not timeout.
 */
class SessionController {
    static defaultAction = 'keepalive'

    /**
     * Keep the session alive.
     */
    def keepalive() {
        render(status: 200)
    }
}
