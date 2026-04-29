create table if not exists textbook_version (
    id varchar(64) primary key,
    code varchar(64) not null,
    name varchar(120) not null,
    subject_code varchar(32) not null,
    status varchar(32) not null,
    is_deleted boolean not null default false,
    deleted_at timestamp null,
    deleted_by varchar(64),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint uk_textbook_version_code unique (code)
);

create table if not exists curriculum_node (
    id varchar(64) primary key,
    textbook_version_id varchar(64) not null,
    parent_node_id varchar(64),
    node_code varchar(64) not null,
    node_name varchar(120) not null,
    node_type varchar(32) not null,
    sort_order integer not null,
    status varchar(32) not null,
    is_deleted boolean not null default false,
    deleted_at timestamp null,
    deleted_by varchar(64),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint fk_curriculum_node_textbook_version foreign key (textbook_version_id) references textbook_version (id),
    constraint fk_curriculum_node_parent foreign key (parent_node_id) references curriculum_node (id),
    constraint uk_curriculum_node_code unique (textbook_version_id, node_code),
    constraint uk_curriculum_node_sort unique (textbook_version_id, parent_node_id, sort_order)
);

create table if not exists content_asset (
    id varchar(64) primary key,
    curriculum_node_id varchar(64) not null,
    asset_type varchar(32) not null,
    title varchar(160) not null,
    body_markdown longtext,
    review_status varchar(32) not null,
    publish_status varchar(32) not null,
    is_deleted boolean not null default false,
    deleted_at timestamp null,
    deleted_by varchar(64),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint fk_content_asset_curriculum_node foreign key (curriculum_node_id) references curriculum_node (id)
);

create table if not exists question_service.question (
    id varchar(64) primary key,
    question_type varchar(32) not null,
    difficulty_level varchar(32) not null,
    source_type varchar(32) not null,
    review_status varchar(32) not null,
    stem_markdown longtext not null,
    grade_code varchar(32) not null,
    subject_code varchar(32) not null,
    is_deleted boolean not null default false,
    deleted_at timestamp null,
    deleted_by varchar(64),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64)
);

create table if not exists question_service.question_option (
    id varchar(64) primary key,
    question_id varchar(64) not null,
    option_code varchar(16) not null,
    option_content longtext not null,
    is_correct boolean not null default false,
    sort_order integer not null,
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint fk_question_option_question foreign key (question_id) references question_service.question (id),
    constraint uk_question_option_code unique (question_id, option_code)
);

create table if not exists question_service.question_answer (
    id varchar(64) primary key,
    question_id varchar(64) not null,
    answer_type varchar(32) not null,
    answer_content longtext not null,
    answer_version integer not null,
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint fk_question_answer_question foreign key (question_id) references question_service.question (id),
    constraint uk_question_answer_version unique (question_id, answer_version)
);

create table if not exists question_service.question_analysis (
    id varchar(64) primary key,
    question_id varchar(64) not null,
    analysis_version integer not null,
    analysis_markdown longtext not null,
    step_markdown longtext,
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint fk_question_analysis_question foreign key (question_id) references question_service.question (id),
    constraint uk_question_analysis_version unique (question_id, analysis_version)
);

create table if not exists question_service.question_knowledge (
    id varchar(64) primary key,
    question_id varchar(64) not null,
    curriculum_node_id varchar(64) not null,
    relation_type varchar(32) not null,
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    constraint fk_question_knowledge_question foreign key (question_id) references question_service.question (id),
    constraint uk_question_knowledge unique (question_id, curriculum_node_id, relation_type)
);

create table if not exists question_service.question_curriculum_node (
    id varchar(64) primary key,
    question_id varchar(64) not null,
    curriculum_node_id varchar(64) not null,
    node_type varchar(32) not null,
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    constraint fk_question_curriculum_node_question foreign key (question_id) references question_service.question (id),
    constraint uk_question_curriculum_node unique (question_id, curriculum_node_id, node_type)
);

create table if not exists exam_service.exam_plan (
    id varchar(64) primary key,
    title varchar(160) not null,
    scope_mode varchar(32) not null,
    textbook_version_id varchar(64) not null,
    status varchar(32) not null,
    start_time timestamp,
    end_time timestamp,
    duration_minutes integer,
    is_deleted boolean not null default false,
    deleted_at timestamp null,
    deleted_by varchar(64),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64)
);

create table if not exists exam_service.exam_plan_target (
    id varchar(64) primary key,
    exam_plan_id varchar(64) not null,
    target_type varchar(32) not null,
    target_ref varchar(64) not null,
    status varchar(32) not null,
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint fk_exam_plan_target_plan foreign key (exam_plan_id) references exam_service.exam_plan (id),
    constraint uk_exam_plan_target unique (exam_plan_id, target_type, target_ref)
);

create table if not exists exam_service.exam_session (
    id varchar(64) primary key,
    exam_plan_id varchar(64) not null,
    paper_id varchar(64),
    status varchar(32) not null,
    start_time timestamp,
    end_time timestamp,
    duration_minutes integer,
    late_arrival_policy varchar(32),
    submit_policy varchar(32),
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint fk_exam_session_plan foreign key (exam_plan_id) references exam_service.exam_plan (id)
);

create table if not exists exam_service.exam_submission (
    id varchar(64) primary key,
    exam_session_id varchar(64) not null,
    student_id varchar(64) not null,
    submit_status varchar(32) not null,
    submitted_at timestamp,
    client_id varchar(64),
    answer_payload longtext,
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint fk_exam_submission_session foreign key (exam_session_id) references exam_service.exam_session (id),
    constraint uk_exam_submission_student unique (exam_session_id, student_id)
);

create table if not exists job_service.job_task (
    id varchar(64) primary key,
    task_type varchar(64) not null,
    status varchar(32) not null,
    idempotency_key varchar(128) not null,
    progress integer not null default 0,
    result_summary varchar(255),
    error_code varchar(64),
    error_message varchar(255),
    retryable boolean not null default false,
    trace_id varchar(128) not null,
    created_at timestamp not null default current_timestamp,
    created_by varchar(64),
    updated_at timestamp not null default current_timestamp,
    updated_by varchar(64),
    constraint uk_job_task_idempotency unique (idempotency_key)
);

create index if not exists idx_curriculum_node_parent on curriculum_node (parent_node_id, sort_order);
create index if not exists idx_content_asset_curriculum_node on content_asset (curriculum_node_id, review_status);
create index if not exists idx_question_review_status on question_service.question (review_status, subject_code);
create index if not exists idx_exam_plan_scope_mode on exam_service.exam_plan (scope_mode, status);
create index if not exists idx_job_task_status on job_service.job_task (status, task_type);
