create table if not exists `user` (
    id varchar(64) primary key,
    username varchar(100) not null,
    display_name varchar(120) not null,
    status varchar(32) not null,
    password_hash varchar(255),
    is_deleted boolean not null default false,
    deleted_at timestamp null,
    deleted_by varchar(64),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint uk_user_username unique (username)
);

create table if not exists `role` (
    id varchar(64) primary key,
    code varchar(64) not null,
    name varchar(120) not null,
    status varchar(32) not null,
    is_deleted boolean not null default false,
    deleted_at timestamp null,
    deleted_by varchar(64),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint uk_role_code unique (code)
);

create table if not exists user_role (
    id varchar(64) primary key,
    user_id varchar(64) not null,
    role_id varchar(64) not null,
    is_deleted boolean not null default false,
    deleted_at timestamp null,
    deleted_by varchar(64),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint fk_user_role_user foreign key (user_id) references `user` (id),
    constraint fk_user_role_role foreign key (role_id) references `role` (id),
    constraint uk_user_role_pair unique (user_id, role_id)
);

create table if not exists class_room (
    id varchar(64) primary key,
    code varchar(64) not null,
    name varchar(120) not null,
    grade_code varchar(32) not null,
    status varchar(32) not null,
    is_deleted boolean not null default false,
    deleted_at timestamp null,
    deleted_by varchar(64),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint uk_class_room_code unique (code)
);

create table if not exists class_membership (
    id varchar(64) primary key,
    class_room_id varchar(64) not null,
    user_id varchar(64) not null,
    membership_role varchar(32) not null,
    status varchar(32) not null,
    is_deleted boolean not null default false,
    deleted_at timestamp null,
    deleted_by varchar(64),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint fk_class_membership_class_room foreign key (class_room_id) references class_room (id),
    constraint fk_class_membership_user foreign key (user_id) references `user` (id),
    constraint uk_class_membership unique (class_room_id, user_id, membership_role)
);

create table if not exists teaching_assignment (
    id varchar(64) primary key,
    teacher_user_id varchar(64) not null,
    class_room_id varchar(64) not null,
    subject_code varchar(32) not null,
    textbook_version_id varchar(64) not null,
    curriculum_root_node_id varchar(64),
    status varchar(32) not null,
    is_deleted boolean not null default false,
    deleted_at timestamp null,
    deleted_by varchar(64),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint fk_teaching_assignment_teacher foreign key (teacher_user_id) references `user` (id),
    constraint fk_teaching_assignment_class_room foreign key (class_room_id) references class_room (id),
    constraint uk_teaching_assignment unique (teacher_user_id, class_room_id, subject_code)
);

create table if not exists resource_owner_scope (
    id varchar(64) primary key,
    resource_type varchar(64) not null,
    resource_id varchar(64) not null,
    owner_user_id varchar(64),
    owner_role_code varchar(64),
    owner_scope_type varchar(32) not null,
    owner_scope_ref varchar(128) not null,
    status varchar(32) not null,
    is_deleted boolean not null default false,
    deleted_at timestamp null,
    deleted_by varchar(64),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint uk_resource_owner_scope unique (resource_type, resource_id, owner_scope_type, owner_scope_ref)
);

create table if not exists audit_log (
    id varchar(64) primary key,
    trace_id varchar(128) not null,
    actor_user_id varchar(64),
    actor_role_code varchar(64),
    action_code varchar(64) not null,
    resource_type varchar(64) not null,
    resource_id varchar(64) not null,
    result_code varchar(32) not null,
    detail_json clob,
    created_at timestamp not null default current_timestamp,
    created_by varchar(64)
);

create index if not exists idx_audit_log_trace_id on audit_log (trace_id);
create index if not exists idx_resource_owner_scope_resource on resource_owner_scope (resource_type, resource_id);
create index if not exists idx_teaching_assignment_teacher on teaching_assignment (teacher_user_id, subject_code);
