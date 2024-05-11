CREATE TABLE IF NOT EXISTS public.users (
    id uuid NOT NULL,
    email character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    image_name character varying(255) COLLATE pg_catalog."default",
    image_type character varying(255) COLLATE pg_catalog."default",
    image_url character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.roles (
    id bigserial NOT NULL,
    authority character varying(255) COLLATE pg_catalog."default",
    description character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT roles_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.user_roles (
    user_id uuid NOT NULL,
    role_id bigint NOT NULL,
    CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT fkh8ciramu9cc9q3qcqiv4ue8a6 FOREIGN KEY (role_id)
    REFERENCES public.roles (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION,
    CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id)
    REFERENCES public.users (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.password_resets (
    id uuid NOT NULL,
    expiry timestamp(6) without time zone,
    token character varying(255) COLLATE pg_catalog."default",
    used boolean NOT NULL,
    used_at timestamp(6) without time zone,
    user_id uuid NOT NULL,
    CONSTRAINT password_resets_pkey PRIMARY KEY (id),
    CONSTRAINT fkfy4ulhbvy3yguwnqqvts2iqqx FOREIGN KEY (user_id)
    REFERENCES public.users (id) MATCH SIMPLE
                        ON UPDATE NO ACTION
                        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.topic (
    id bigserial NOT NULL,
    description character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    topic_url character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT topic_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.venues (
    id uuid NOT NULL,
    address character varying(255) COLLATE pg_catalog."default",
    city character varying(255) COLLATE pg_catalog."default",
    country character varying(255) COLLATE pg_catalog."default",
    latitude character varying(255) COLLATE pg_catalog."default",
    longitude character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    state character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT venues_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.events (
    id uuid NOT NULL,
    description text COLLATE pg_catalog."default",
    end_date timestamp(6) with time zone,
                              event_cost character varying(255) COLLATE pg_catalog."default",
    event_location character varying(255) COLLATE pg_catalog."default",
    event_status character varying(255) COLLATE pg_catalog."default",
    event_url character varying(255) COLLATE pg_catalog."default",
    host uuid,
    slug character varying(255) COLLATE pg_catalog."default",
    start_date timestamp(6) with time zone,
                              title character varying(255) COLLATE pg_catalog."default",
    venue_id uuid,
    attendance_limit integer,
    event_attendance character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT events_pkey PRIMARY KEY (id),
    CONSTRAINT uk_qka83gbjmaflc1ko37xkpojof UNIQUE (venue_id),
    CONSTRAINT fkqdxygdernwwt74hdvix9u5nr3 FOREIGN KEY (venue_id)
    REFERENCES public.venues (id) MATCH SIMPLE
                          ON UPDATE NO ACTION
                          ON DELETE NO ACTION,
    CONSTRAINT events_event_cost_check CHECK (event_cost::text = ANY (ARRAY['FREE'::character varying, 'PAID'::character varying]::text[])),
    CONSTRAINT events_event_location_check CHECK (event_location::text = ANY (ARRAY['PHYSICAL'::character varying, 'VIRTUAL'::character varying]::text[])),
    CONSTRAINT events_event_status_check CHECK (event_status::text = ANY (ARRAY['ACTIVE'::character varying, 'INPROGRESS'::character varying, 'COMPLETE'::character varying, 'CANCELLED'::character varying]::text[])),
    CONSTRAINT events_event_attendance_check CHECK (event_attendance::text = ANY (ARRAY['LIMITED'::character varying, 'UNLIMITED'::character varying]::text[]))
);

CREATE TABLE IF NOT EXISTS public.event_topics (
    event_id uuid NOT NULL,
    topics_id bigint NOT NULL,
    CONSTRAINT event_topics_pkey PRIMARY KEY (event_id, topics_id),
    CONSTRAINT fk77688s2tt2de2e4or1a1u22hi FOREIGN KEY (topics_id)
    REFERENCES public.topic (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION,
    CONSTRAINT fkh1d357nti9wfppyye1v4ryww FOREIGN KEY (event_id)
    REFERENCES public.events (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.event_photos (
    id bigserial NOT NULL,
    event_photo_type character varying(255) COLLATE pg_catalog."default",
    image_name character varying(255) COLLATE pg_catalog."default",
    image_type character varying(255) COLLATE pg_catalog."default",
    image_url character varying(255) COLLATE pg_catalog."default",
    event_id uuid,
    CONSTRAINT event_photos_pkey PRIMARY KEY (id),
    CONSTRAINT fk7sqgrrutdbot04bosfqcd4i6v FOREIGN KEY (event_id)
    REFERENCES public.events (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION,
    CONSTRAINT event_photos_event_photo_type_check CHECK (event_photo_type::text = ANY (ARRAY['FEATURED'::character varying, 'OTHER'::character varying]::text[]))
);

CREATE TABLE IF NOT EXISTS public.tickets (
    id uuid NOT NULL,
    currency character varying(255) COLLATE pg_catalog."default",
    number_of_purchased_tickets integer,
    number_of_tickets integer,
    price numeric(38,2),
    ticket_close timestamp(6) without time zone,
    title character varying(255) COLLATE pg_catalog."default",
    event_id uuid,
    CONSTRAINT tickets_pkey PRIMARY KEY (id),
    CONSTRAINT fk3utafe14rupaypjocldjaj4ol FOREIGN KEY (event_id)
    REFERENCES public.events (id) MATCH SIMPLE
                              ON UPDATE NO ACTION
                              ON DELETE NO ACTION,
    CONSTRAINT tickets_currency_check CHECK (currency::text = ANY (ARRAY['EUR'::character varying, 'GBP'::character varying, 'KSH'::character varying, 'USD'::character varying]::text[]))
);

CREATE TABLE IF NOT EXISTS public.reservations (
    id uuid NOT NULL,
    attendance_confirmation_status character varying(255) COLLATE pg_catalog."default",
    event_id uuid,
    reservation_status character varying(255) COLLATE pg_catalog."default",
    reservation_type character varying(255) COLLATE pg_catalog."default",
    reserved_at timestamp(6) with time zone,
                                 user_id uuid,
                                 CONSTRAINT reservations_pkey PRIMARY KEY (id),
    CONSTRAINT reservations_attendance_confirmation_status_check CHECK (attendance_confirmation_status::text = ANY (ARRAY['CONFIRMED'::character varying, 'UNCONFIRMED'::character varying]::text[])),
    CONSTRAINT reservations_reservation_status_check CHECK (reservation_status::text = ANY (ARRAY['RESERVED'::character varying, 'PENDING_PAYMENT'::character varying]::text[])),
    CONSTRAINT reservations_reservation_type_check CHECK (reservation_type::text = ANY (ARRAY['PAID'::character varying, 'FREE'::character varying]::text[]))
);
