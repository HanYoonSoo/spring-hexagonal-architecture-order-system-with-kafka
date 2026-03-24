alter table outbox_event
alter column occurred_at type timestamp
using occurred_at;
