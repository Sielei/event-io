package com.hs.eventio.events;

class EventConstants {
    public enum EventStatus {
        ACTIVE, INPROGRESS, COMPLETE, CANCELLED
    }
    public enum EventLocation {
        PHYSICAL, VIRTUAL
    }
    public enum EventCost {
        FREE, PAID
    }
    public enum EventPhotoType {
        FEATURED, OTHER
    }
    public enum EventAttendance {
        LIMITED, UNLIMITED
    }
}
