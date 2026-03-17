update outbox_event
set payload = convert_from(lo_get(payload::oid), 'UTF8')
where payload ~ '^[0-9]+$'
  and exists (
      select 1
      from pg_largeobject_metadata metadata
      where metadata.oid = payload::oid
  );
