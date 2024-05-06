package com.hs.eventio.events;

import org.springframework.data.jpa.repository.JpaRepository;

interface EventPhotoRepository extends JpaRepository<EventPhoto, Long> {
}