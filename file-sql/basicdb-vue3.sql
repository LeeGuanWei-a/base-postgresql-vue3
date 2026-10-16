--
-- PostgreSQL database dump
--

-- Dumped from database version 15.1
-- Dumped by pg_dump version 17.0

-- Started on 2026-10-16 09:50:43

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE ONLY basicdb_flowable.flw_ru_batch_part DROP CONSTRAINT flw_fk_batch_part_parent;
ALTER TABLE ONLY basicdb_flowable.act_ru_variable DROP CONSTRAINT act_fk_var_procinst;
ALTER TABLE ONLY basicdb_flowable.act_ru_variable DROP CONSTRAINT act_fk_var_exe;
ALTER TABLE ONLY basicdb_flowable.act_ru_variable DROP CONSTRAINT act_fk_var_bytearray;
ALTER TABLE ONLY basicdb_flowable.act_ru_identitylink DROP CONSTRAINT act_fk_tskass_task;
ALTER TABLE ONLY basicdb_flowable.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_process_instance;
ALTER TABLE ONLY basicdb_flowable.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_proc_def;
ALTER TABLE ONLY basicdb_flowable.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_execution;
ALTER TABLE ONLY basicdb_flowable.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_exception;
ALTER TABLE ONLY basicdb_flowable.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_custom_values;
ALTER TABLE ONLY basicdb_flowable.act_ru_task DROP CONSTRAINT act_fk_task_procinst;
ALTER TABLE ONLY basicdb_flowable.act_ru_task DROP CONSTRAINT act_fk_task_procdef;
ALTER TABLE ONLY basicdb_flowable.act_ru_task DROP CONSTRAINT act_fk_task_exe;
ALTER TABLE ONLY basicdb_flowable.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_process_instance;
ALTER TABLE ONLY basicdb_flowable.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_proc_def;
ALTER TABLE ONLY basicdb_flowable.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_execution;
ALTER TABLE ONLY basicdb_flowable.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_exception;
ALTER TABLE ONLY basicdb_flowable.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_custom_values;
ALTER TABLE ONLY basicdb_flowable.act_re_model DROP CONSTRAINT act_fk_model_source_extra;
ALTER TABLE ONLY basicdb_flowable.act_re_model DROP CONSTRAINT act_fk_model_source;
ALTER TABLE ONLY basicdb_flowable.act_re_model DROP CONSTRAINT act_fk_model_deployment;
ALTER TABLE ONLY basicdb_flowable.act_ru_job DROP CONSTRAINT act_fk_job_process_instance;
ALTER TABLE ONLY basicdb_flowable.act_ru_job DROP CONSTRAINT act_fk_job_proc_def;
ALTER TABLE ONLY basicdb_flowable.act_ru_job DROP CONSTRAINT act_fk_job_execution;
ALTER TABLE ONLY basicdb_flowable.act_ru_job DROP CONSTRAINT act_fk_job_exception;
ALTER TABLE ONLY basicdb_flowable.act_ru_job DROP CONSTRAINT act_fk_job_custom_values;
ALTER TABLE ONLY basicdb_flowable.act_procdef_info DROP CONSTRAINT act_fk_info_procdef;
ALTER TABLE ONLY basicdb_flowable.act_procdef_info DROP CONSTRAINT act_fk_info_json_ba;
ALTER TABLE ONLY basicdb_flowable.act_ru_identitylink DROP CONSTRAINT act_fk_idl_procinst;
ALTER TABLE ONLY basicdb_flowable.act_ru_external_job DROP CONSTRAINT act_fk_external_job_exception;
ALTER TABLE ONLY basicdb_flowable.act_ru_external_job DROP CONSTRAINT act_fk_external_job_custom_values;
ALTER TABLE ONLY basicdb_flowable.act_ru_execution DROP CONSTRAINT act_fk_exe_super;
ALTER TABLE ONLY basicdb_flowable.act_ru_execution DROP CONSTRAINT act_fk_exe_procinst;
ALTER TABLE ONLY basicdb_flowable.act_ru_execution DROP CONSTRAINT act_fk_exe_procdef;
ALTER TABLE ONLY basicdb_flowable.act_ru_execution DROP CONSTRAINT act_fk_exe_parent;
ALTER TABLE ONLY basicdb_flowable.act_ru_event_subscr DROP CONSTRAINT act_fk_event_exec;
ALTER TABLE ONLY basicdb_flowable.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_process_instance;
ALTER TABLE ONLY basicdb_flowable.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_proc_def;
ALTER TABLE ONLY basicdb_flowable.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_execution;
ALTER TABLE ONLY basicdb_flowable.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_exception;
ALTER TABLE ONLY basicdb_flowable.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_custom_values;
ALTER TABLE ONLY basicdb_flowable.act_ge_bytearray DROP CONSTRAINT act_fk_bytearr_depl;
ALTER TABLE ONLY basicdb_flowable.act_ru_identitylink DROP CONSTRAINT act_fk_athrz_procedef;
DROP INDEX basicdb_flowable.uk_sys_notice_read_notice_user;
DROP INDEX basicdb_flowable.sched_name;
DROP INDEX basicdb_flowable.idx_wf_task_form_task_id;
DROP INDEX basicdb_flowable.idx_wf_task_form_proc_ins_id;
DROP INDEX basicdb_flowable.idx_wf_deploy_form_form_id;
DROP INDEX basicdb_flowable.idx_wf_deploy_form_deploy_id;
DROP INDEX basicdb_flowable.idx_wf_copy_user_id;
DROP INDEX basicdb_flowable.idx_wf_copy_instance_id;
DROP INDEX basicdb_flowable.idx_wf_comment_info_task_id;
DROP INDEX basicdb_flowable.idx_wf_comment_info_proc_ins_id;
DROP INDEX basicdb_flowable.idx_wf_category_org_link_org_id;
DROP INDEX basicdb_flowable.idx_wf_category_org_link_category_id;
DROP INDEX basicdb_flowable.idx_tbl_flow_extend_procinst_ins_id;
DROP INDEX basicdb_flowable.idx_sys_files_task;
DROP INDEX basicdb_flowable.idx_sys_files_proc_inst;
DROP INDEX basicdb_flowable.idx_sys_files_activity;
DROP INDEX basicdb_flowable.flw_idx_batch_part;
DROP INDEX basicdb_flowable.dict_type;
DROP INDEX basicdb_flowable.act_idx_variable_task_id;
DROP INDEX basicdb_flowable.act_idx_var_procinst;
DROP INDEX basicdb_flowable.act_idx_var_exe;
DROP INDEX basicdb_flowable.act_idx_var_bytearray;
DROP INDEX basicdb_flowable.act_idx_tskass_task;
DROP INDEX basicdb_flowable.act_idx_tjob_sub_scope;
DROP INDEX basicdb_flowable.act_idx_tjob_scope_def;
DROP INDEX basicdb_flowable.act_idx_tjob_scope;
DROP INDEX basicdb_flowable.act_idx_timer_job_process_instance_id;
DROP INDEX basicdb_flowable.act_idx_timer_job_proc_def_id;
DROP INDEX basicdb_flowable.act_idx_timer_job_execution_id;
DROP INDEX basicdb_flowable.act_idx_timer_job_exception_stack_id;
DROP INDEX basicdb_flowable.act_idx_timer_job_duedate;
DROP INDEX basicdb_flowable.act_idx_timer_job_custom_values_id;
DROP INDEX basicdb_flowable.act_idx_timer_job_correlation_id;
DROP INDEX basicdb_flowable.act_idx_task_sub_scope;
DROP INDEX basicdb_flowable.act_idx_task_scope_def;
DROP INDEX basicdb_flowable.act_idx_task_scope;
DROP INDEX basicdb_flowable.act_idx_task_procinst;
DROP INDEX basicdb_flowable.act_idx_task_procdef;
DROP INDEX basicdb_flowable.act_idx_task_exec;
DROP INDEX basicdb_flowable.act_idx_task_create;
DROP INDEX basicdb_flowable.act_idx_suspended_job_process_instance_id;
DROP INDEX basicdb_flowable.act_idx_suspended_job_proc_def_id;
DROP INDEX basicdb_flowable.act_idx_suspended_job_execution_id;
DROP INDEX basicdb_flowable.act_idx_suspended_job_exception_stack_id;
DROP INDEX basicdb_flowable.act_idx_suspended_job_custom_values_id;
DROP INDEX basicdb_flowable.act_idx_suspended_job_correlation_id;
DROP INDEX basicdb_flowable.act_idx_sjob_sub_scope;
DROP INDEX basicdb_flowable.act_idx_sjob_scope_def;
DROP INDEX basicdb_flowable.act_idx_sjob_scope;
DROP INDEX basicdb_flowable.act_idx_ru_var_sub_id_type;
DROP INDEX basicdb_flowable.act_idx_ru_var_scope_id_type;
DROP INDEX basicdb_flowable.act_idx_ru_acti_task;
DROP INDEX basicdb_flowable.act_idx_ru_acti_start;
DROP INDEX basicdb_flowable.act_idx_ru_acti_proc_act;
DROP INDEX basicdb_flowable.act_idx_ru_acti_proc;
DROP INDEX basicdb_flowable.act_idx_ru_acti_exec_act;
DROP INDEX basicdb_flowable.act_idx_ru_acti_exec;
DROP INDEX basicdb_flowable.act_idx_ru_acti_end;
DROP INDEX basicdb_flowable.act_idx_procdef_info_proc;
DROP INDEX basicdb_flowable.act_idx_procdef_info_json;
DROP INDEX basicdb_flowable.act_idx_model_source_extra;
DROP INDEX basicdb_flowable.act_idx_model_source;
DROP INDEX basicdb_flowable.act_idx_model_deployment;
DROP INDEX basicdb_flowable.act_idx_job_sub_scope;
DROP INDEX basicdb_flowable.act_idx_job_scope_def;
DROP INDEX basicdb_flowable.act_idx_job_scope;
DROP INDEX basicdb_flowable.act_idx_job_process_instance_id;
DROP INDEX basicdb_flowable.act_idx_job_proc_def_id;
DROP INDEX basicdb_flowable.act_idx_job_execution_id;
DROP INDEX basicdb_flowable.act_idx_job_exception_stack_id;
DROP INDEX basicdb_flowable.act_idx_job_custom_values_id;
DROP INDEX basicdb_flowable.act_idx_job_correlation_id;
DROP INDEX basicdb_flowable.act_idx_idl_procinst;
DROP INDEX basicdb_flowable.act_idx_ident_lnk_user;
DROP INDEX basicdb_flowable.act_idx_ident_lnk_sub_scope;
DROP INDEX basicdb_flowable.act_idx_ident_lnk_scope_def;
DROP INDEX basicdb_flowable.act_idx_ident_lnk_scope;
DROP INDEX basicdb_flowable.act_idx_ident_lnk_group;
DROP INDEX basicdb_flowable.act_idx_hi_var_sub_id_type;
DROP INDEX basicdb_flowable.act_idx_hi_var_scope_id_type;
DROP INDEX basicdb_flowable.act_idx_hi_task_sub_scope;
DROP INDEX basicdb_flowable.act_idx_hi_task_scope_def;
DROP INDEX basicdb_flowable.act_idx_hi_task_scope;
DROP INDEX basicdb_flowable.act_idx_hi_task_inst_procinst;
DROP INDEX basicdb_flowable.act_idx_hi_procvar_task_id;
DROP INDEX basicdb_flowable.act_idx_hi_procvar_proc_inst;
DROP INDEX basicdb_flowable.act_idx_hi_procvar_name_type;
DROP INDEX basicdb_flowable.act_idx_hi_procvar_exe;
DROP INDEX basicdb_flowable.act_idx_hi_pro_super_procinst;
DROP INDEX basicdb_flowable.act_idx_hi_pro_inst_end;
DROP INDEX basicdb_flowable.act_idx_hi_pro_i_buskey;
DROP INDEX basicdb_flowable.act_idx_hi_ident_lnk_user;
DROP INDEX basicdb_flowable.act_idx_hi_ident_lnk_task;
DROP INDEX basicdb_flowable.act_idx_hi_ident_lnk_sub_scope;
DROP INDEX basicdb_flowable.act_idx_hi_ident_lnk_scope_def;
DROP INDEX basicdb_flowable.act_idx_hi_ident_lnk_scope;
DROP INDEX basicdb_flowable.act_idx_hi_ident_lnk_procinst;
DROP INDEX basicdb_flowable.act_idx_hi_ent_lnk_scope_def;
DROP INDEX basicdb_flowable.act_idx_hi_ent_lnk_scope;
DROP INDEX basicdb_flowable.act_idx_hi_ent_lnk_root_scope;
DROP INDEX basicdb_flowable.act_idx_hi_ent_lnk_ref_scope;
DROP INDEX basicdb_flowable.act_idx_hi_detail_time;
DROP INDEX basicdb_flowable.act_idx_hi_detail_task_id;
DROP INDEX basicdb_flowable.act_idx_hi_detail_proc_inst;
DROP INDEX basicdb_flowable.act_idx_hi_detail_name;
DROP INDEX basicdb_flowable.act_idx_hi_detail_act_inst;
DROP INDEX basicdb_flowable.act_idx_hi_act_inst_start;
DROP INDEX basicdb_flowable.act_idx_hi_act_inst_procinst;
DROP INDEX basicdb_flowable.act_idx_hi_act_inst_exec;
DROP INDEX basicdb_flowable.act_idx_hi_act_inst_end;
DROP INDEX basicdb_flowable.act_idx_external_job_exception_stack_id;
DROP INDEX basicdb_flowable.act_idx_external_job_custom_values_id;
DROP INDEX basicdb_flowable.act_idx_external_job_correlation_id;
DROP INDEX basicdb_flowable.act_idx_exec_ref_id_;
DROP INDEX basicdb_flowable.act_idx_exec_buskey;
DROP INDEX basicdb_flowable.act_idx_exe_super;
DROP INDEX basicdb_flowable.act_idx_exe_root;
DROP INDEX basicdb_flowable.act_idx_exe_procinst;
DROP INDEX basicdb_flowable.act_idx_exe_procdef;
DROP INDEX basicdb_flowable.act_idx_exe_parent;
DROP INDEX basicdb_flowable.act_idx_event_subscr_scoperef_;
DROP INDEX basicdb_flowable.act_idx_event_subscr_config_;
DROP INDEX basicdb_flowable.act_idx_event_subscr;
DROP INDEX basicdb_flowable.act_idx_event_def_uniq;
DROP INDEX basicdb_flowable.act_idx_ent_lnk_scope_def;
DROP INDEX basicdb_flowable.act_idx_ent_lnk_scope;
DROP INDEX basicdb_flowable.act_idx_ent_lnk_root_scope;
DROP INDEX basicdb_flowable.act_idx_ent_lnk_ref_scope;
DROP INDEX basicdb_flowable.act_idx_ejob_sub_scope;
DROP INDEX basicdb_flowable.act_idx_ejob_scope_def;
DROP INDEX basicdb_flowable.act_idx_ejob_scope;
DROP INDEX basicdb_flowable.act_idx_djob_sub_scope;
DROP INDEX basicdb_flowable.act_idx_djob_scope_def;
DROP INDEX basicdb_flowable.act_idx_djob_scope;
DROP INDEX basicdb_flowable.act_idx_deadletter_job_process_instance_id;
DROP INDEX basicdb_flowable.act_idx_deadletter_job_proc_def_id;
DROP INDEX basicdb_flowable.act_idx_deadletter_job_execution_id;
DROP INDEX basicdb_flowable.act_idx_deadletter_job_exception_stack_id;
DROP INDEX basicdb_flowable.act_idx_deadletter_job_custom_values_id;
DROP INDEX basicdb_flowable.act_idx_deadletter_job_correlation_id;
DROP INDEX basicdb_flowable.act_idx_de_model_type;
DROP INDEX basicdb_flowable.act_idx_de_model_tenant;
DROP INDEX basicdb_flowable.act_idx_de_model_relation_parent;
DROP INDEX basicdb_flowable.act_idx_de_model_key;
DROP INDEX basicdb_flowable.act_idx_de_model_history_model_id;
DROP INDEX basicdb_flowable.act_idx_channel_def_uniq;
DROP INDEX basicdb_flowable.act_idx_bytear_depl;
DROP INDEX basicdb_flowable.act_idx_athrz_procedef;
ALTER TABLE ONLY basicdb_flowable.wf_task_form DROP CONSTRAINT wf_task_form_pkey;
ALTER TABLE ONLY basicdb_flowable.wf_form_template DROP CONSTRAINT wf_form_template_pkey;
ALTER TABLE ONLY basicdb_flowable.wf_form DROP CONSTRAINT wf_form_pkey;
ALTER TABLE ONLY basicdb_flowable.wf_form_category DROP CONSTRAINT wf_form_category_pkey;
ALTER TABLE ONLY basicdb_flowable.wf_deploy_form DROP CONSTRAINT wf_deploy_form_pkey;
ALTER TABLE ONLY basicdb_flowable.wf_copy DROP CONSTRAINT wf_copy_pkey;
ALTER TABLE ONLY basicdb_flowable.wf_comment_info DROP CONSTRAINT wf_comment_info_pkey;
ALTER TABLE ONLY basicdb_flowable.wf_category DROP CONSTRAINT wf_category_pkey;
ALTER TABLE ONLY basicdb_flowable.wf_category_org_link DROP CONSTRAINT wf_category_org_link_pkey;
ALTER TABLE ONLY basicdb_flowable.tbl_flow_extend_procinst DROP CONSTRAINT tbl_flow_extend_procinst_pkey;
ALTER TABLE ONLY basicdb_flowable.sys_notice_read DROP CONSTRAINT sys_notice_read_pkey;
ALTER TABLE ONLY basicdb_flowable.sys_files DROP CONSTRAINT sys_files_pkey;
ALTER TABLE ONLY basicdb_flowable.sys_demo DROP CONSTRAINT sys_demo_pkey;
ALTER TABLE ONLY basicdb_flowable.flw_ru_batch DROP CONSTRAINT flw_ru_batch_pkey;
ALTER TABLE ONLY basicdb_flowable.flw_ru_batch_part DROP CONSTRAINT flw_ru_batch_part_pkey;
ALTER TABLE ONLY basicdb_flowable.flw_expression DROP CONSTRAINT flw_expression_pkey;
ALTER TABLE ONLY basicdb_flowable.flw_event_resource DROP CONSTRAINT flw_event_resource_pkey;
ALTER TABLE ONLY basicdb_flowable.flw_event_deployment DROP CONSTRAINT flw_event_deployment_pkey;
ALTER TABLE ONLY basicdb_flowable.flw_event_definition DROP CONSTRAINT flw_event_definition_pkey;
ALTER TABLE ONLY basicdb_flowable.flw_ev_databasechangeloglock DROP CONSTRAINT flw_ev_databasechangeloglock_pkey;
ALTER TABLE ONLY basicdb_flowable.flw_channel_definition DROP CONSTRAINT flw_channel_definition_pkey;
ALTER TABLE ONLY basicdb_flowable.act_re_procdef DROP CONSTRAINT act_uniq_procdef;
ALTER TABLE ONLY basicdb_flowable.act_procdef_info DROP CONSTRAINT act_uniq_info_procdef;
ALTER TABLE ONLY basicdb_flowable.act_ru_variable DROP CONSTRAINT act_ru_variable_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ru_timer_job DROP CONSTRAINT act_ru_timer_job_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ru_task DROP CONSTRAINT act_ru_task_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ru_suspended_job DROP CONSTRAINT act_ru_suspended_job_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ru_job DROP CONSTRAINT act_ru_job_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ru_identitylink DROP CONSTRAINT act_ru_identitylink_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ru_history_job DROP CONSTRAINT act_ru_history_job_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ru_external_job DROP CONSTRAINT act_ru_external_job_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ru_execution DROP CONSTRAINT act_ru_execution_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ru_event_subscr DROP CONSTRAINT act_ru_event_subscr_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ru_entitylink DROP CONSTRAINT act_ru_entitylink_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ru_deadletter_job DROP CONSTRAINT act_ru_deadletter_job_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ru_actinst DROP CONSTRAINT act_ru_actinst_pkey;
ALTER TABLE ONLY basicdb_flowable.act_re_procdef DROP CONSTRAINT act_re_procdef_pkey;
ALTER TABLE ONLY basicdb_flowable.act_re_model DROP CONSTRAINT act_re_model_pkey;
ALTER TABLE ONLY basicdb_flowable.act_re_deployment DROP CONSTRAINT act_re_deployment_pkey;
ALTER TABLE ONLY basicdb_flowable.act_procdef_info DROP CONSTRAINT act_procdef_info_pkey;
ALTER TABLE ONLY basicdb_flowable.act_hi_varinst DROP CONSTRAINT act_hi_varinst_pkey;
ALTER TABLE ONLY basicdb_flowable.act_hi_tsk_log DROP CONSTRAINT act_hi_tsk_log_pkey;
ALTER TABLE ONLY basicdb_flowable.act_hi_taskinst DROP CONSTRAINT act_hi_taskinst_pkey;
ALTER TABLE ONLY basicdb_flowable.act_hi_procinst DROP CONSTRAINT act_hi_procinst_proc_inst_id__key;
ALTER TABLE ONLY basicdb_flowable.act_hi_procinst DROP CONSTRAINT act_hi_procinst_pkey;
ALTER TABLE ONLY basicdb_flowable.act_hi_identitylink DROP CONSTRAINT act_hi_identitylink_pkey;
ALTER TABLE ONLY basicdb_flowable.act_hi_entitylink DROP CONSTRAINT act_hi_entitylink_pkey;
ALTER TABLE ONLY basicdb_flowable.act_hi_detail DROP CONSTRAINT act_hi_detail_pkey;
ALTER TABLE ONLY basicdb_flowable.act_hi_comment DROP CONSTRAINT act_hi_comment_pkey;
ALTER TABLE ONLY basicdb_flowable.act_hi_attachment DROP CONSTRAINT act_hi_attachment_pkey;
ALTER TABLE ONLY basicdb_flowable.act_hi_actinst DROP CONSTRAINT act_hi_actinst_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ge_property DROP CONSTRAINT act_ge_property_pkey;
ALTER TABLE ONLY basicdb_flowable.act_ge_bytearray DROP CONSTRAINT act_ge_bytearray_pkey;
ALTER TABLE ONLY basicdb_flowable.act_evt_log DROP CONSTRAINT act_evt_log_pkey;
ALTER TABLE ONLY basicdb_flowable.act_de_model_relation DROP CONSTRAINT act_de_model_relation_pkey;
ALTER TABLE ONLY basicdb_flowable.act_de_model DROP CONSTRAINT act_de_model_pkey;
ALTER TABLE ONLY basicdb_flowable.act_de_model_history DROP CONSTRAINT act_de_model_history_pkey;
ALTER TABLE ONLY basicdb_flowable.sys_user_role DROP CONSTRAINT "PRIMARY00028";
ALTER TABLE ONLY basicdb_flowable.sys_user_post DROP CONSTRAINT "PRIMARY00027";
ALTER TABLE ONLY basicdb_flowable.sys_user DROP CONSTRAINT "PRIMARY00026";
ALTER TABLE ONLY basicdb_flowable.sys_role_menu DROP CONSTRAINT "PRIMARY00025";
ALTER TABLE ONLY basicdb_flowable.sys_role_dept DROP CONSTRAINT "PRIMARY00024";
ALTER TABLE ONLY basicdb_flowable.sys_role DROP CONSTRAINT "PRIMARY00023";
ALTER TABLE ONLY basicdb_flowable.sys_post DROP CONSTRAINT "PRIMARY00022";
ALTER TABLE ONLY basicdb_flowable.sys_oper_log DROP CONSTRAINT "PRIMARY00021";
ALTER TABLE ONLY basicdb_flowable.sys_notice DROP CONSTRAINT "PRIMARY00020";
ALTER TABLE ONLY basicdb_flowable.sys_menu DROP CONSTRAINT "PRIMARY00019";
ALTER TABLE ONLY basicdb_flowable.sys_logininfor DROP CONSTRAINT "PRIMARY00018";
ALTER TABLE ONLY basicdb_flowable.sys_job_log DROP CONSTRAINT "PRIMARY00017";
ALTER TABLE ONLY basicdb_flowable.sys_job DROP CONSTRAINT "PRIMARY00016";
ALTER TABLE ONLY basicdb_flowable.sys_dict_type DROP CONSTRAINT "PRIMARY00015";
ALTER TABLE ONLY basicdb_flowable.sys_dict_data DROP CONSTRAINT "PRIMARY00014";
ALTER TABLE ONLY basicdb_flowable.sys_dept DROP CONSTRAINT "PRIMARY00013";
ALTER TABLE ONLY basicdb_flowable.sys_config DROP CONSTRAINT "PRIMARY00012";
ALTER TABLE ONLY basicdb_flowable.qrtz_triggers DROP CONSTRAINT "PRIMARY00011";
ALTER TABLE ONLY basicdb_flowable.qrtz_simprop_triggers DROP CONSTRAINT "PRIMARY00010";
ALTER TABLE ONLY basicdb_flowable.qrtz_simple_triggers DROP CONSTRAINT "PRIMARY00009";
ALTER TABLE ONLY basicdb_flowable.qrtz_scheduler_state DROP CONSTRAINT "PRIMARY00008";
ALTER TABLE ONLY basicdb_flowable.qrtz_paused_trigger_grps DROP CONSTRAINT "PRIMARY00007";
ALTER TABLE ONLY basicdb_flowable.qrtz_locks DROP CONSTRAINT "PRIMARY00006";
ALTER TABLE ONLY basicdb_flowable.qrtz_job_details DROP CONSTRAINT "PRIMARY00005";
ALTER TABLE ONLY basicdb_flowable.qrtz_fired_triggers DROP CONSTRAINT "PRIMARY00004";
ALTER TABLE ONLY basicdb_flowable.qrtz_cron_triggers DROP CONSTRAINT "PRIMARY00003";
ALTER TABLE ONLY basicdb_flowable.qrtz_calendars DROP CONSTRAINT "PRIMARY00002";
ALTER TABLE ONLY basicdb_flowable.qrtz_blob_triggers DROP CONSTRAINT "PRIMARY00001";
ALTER TABLE ONLY basicdb_flowable.gen_table_column DROP CONSTRAINT "PRIMARY00000";
ALTER TABLE ONLY basicdb_flowable.gen_table DROP CONSTRAINT "PRIMARY";
ALTER TABLE basicdb_flowable.sys_user ALTER COLUMN user_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.sys_role ALTER COLUMN role_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.sys_post ALTER COLUMN post_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.sys_oper_log ALTER COLUMN oper_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.sys_notice ALTER COLUMN notice_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.sys_menu ALTER COLUMN menu_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.sys_logininfor ALTER COLUMN info_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.sys_job_log ALTER COLUMN job_log_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.sys_job ALTER COLUMN job_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.sys_dict_type ALTER COLUMN dict_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.sys_dict_data ALTER COLUMN dict_code DROP DEFAULT;
ALTER TABLE basicdb_flowable.sys_dept ALTER COLUMN dept_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.sys_config ALTER COLUMN config_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.gen_table_column ALTER COLUMN column_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.gen_table ALTER COLUMN table_id DROP DEFAULT;
ALTER TABLE basicdb_flowable.act_hi_tsk_log ALTER COLUMN id_ DROP DEFAULT;
ALTER TABLE basicdb_flowable.act_evt_log ALTER COLUMN log_nr_ DROP DEFAULT;
DROP TABLE basicdb_flowable.wf_task_form;
DROP TABLE basicdb_flowable.wf_form_template;
DROP TABLE basicdb_flowable.wf_form_category;
DROP TABLE basicdb_flowable.wf_form;
DROP TABLE basicdb_flowable.wf_deploy_form;
DROP TABLE basicdb_flowable.wf_copy;
DROP TABLE basicdb_flowable.wf_comment_info;
DROP TABLE basicdb_flowable.wf_category_org_link;
DROP TABLE basicdb_flowable.wf_category;
DROP TABLE basicdb_flowable.tbl_flow_extend_procinst;
DROP SEQUENCE basicdb_flowable.sys_user_user_id_seq;
DROP TABLE basicdb_flowable.sys_user_role;
DROP TABLE basicdb_flowable.sys_user_post;
DROP TABLE basicdb_flowable.sys_user;
DROP SEQUENCE basicdb_flowable.sys_role_role_id_seq;
DROP TABLE basicdb_flowable.sys_role_menu;
DROP TABLE basicdb_flowable.sys_role_dept;
DROP TABLE basicdb_flowable.sys_role;
DROP SEQUENCE basicdb_flowable.sys_post_post_id_seq;
DROP TABLE basicdb_flowable.sys_post;
DROP SEQUENCE basicdb_flowable.sys_oper_log_oper_id_seq;
DROP TABLE basicdb_flowable.sys_oper_log;
DROP SEQUENCE basicdb_flowable.sys_notice_read_read_id_seq;
DROP TABLE basicdb_flowable.sys_notice_read;
DROP SEQUENCE basicdb_flowable.sys_notice_notice_id_seq;
DROP TABLE basicdb_flowable.sys_notice;
DROP SEQUENCE basicdb_flowable.sys_menu_menu_id_seq;
DROP TABLE basicdb_flowable.sys_menu;
DROP SEQUENCE basicdb_flowable.sys_logininfor_info_id_seq;
DROP TABLE basicdb_flowable.sys_logininfor;
DROP SEQUENCE basicdb_flowable.sys_job_log_job_log_id_seq;
DROP TABLE basicdb_flowable.sys_job_log;
DROP SEQUENCE basicdb_flowable.sys_job_job_id_seq;
DROP TABLE basicdb_flowable.sys_job;
DROP TABLE basicdb_flowable.sys_i18n;
DROP TABLE basicdb_flowable.sys_files;
DROP SEQUENCE basicdb_flowable.sys_dict_type_dict_id_seq;
DROP TABLE basicdb_flowable.sys_dict_type;
DROP SEQUENCE basicdb_flowable.sys_dict_data_dict_code_seq;
DROP TABLE basicdb_flowable.sys_dict_data;
DROP SEQUENCE basicdb_flowable.sys_dept_dept_id_seq;
DROP TABLE basicdb_flowable.sys_dept;
DROP TABLE basicdb_flowable.sys_demo;
DROP SEQUENCE basicdb_flowable.sys_config_config_id_seq;
DROP TABLE basicdb_flowable.sys_config;
DROP TABLE basicdb_flowable.qrtz_triggers;
DROP TABLE basicdb_flowable.qrtz_simprop_triggers;
DROP TABLE basicdb_flowable.qrtz_simple_triggers;
DROP TABLE basicdb_flowable.qrtz_scheduler_state;
DROP TABLE basicdb_flowable.qrtz_paused_trigger_grps;
DROP TABLE basicdb_flowable.qrtz_locks;
DROP TABLE basicdb_flowable.qrtz_job_details;
DROP TABLE basicdb_flowable.qrtz_fired_triggers;
DROP TABLE basicdb_flowable.qrtz_cron_triggers;
DROP TABLE basicdb_flowable.qrtz_calendars;
DROP TABLE basicdb_flowable.qrtz_blob_triggers;
DROP VIEW basicdb_flowable.list_table;
DROP VIEW basicdb_flowable.list_column;
DROP SEQUENCE basicdb_flowable.gen_table_table_id_seq;
DROP SEQUENCE basicdb_flowable.gen_table_column_column_id_seq;
DROP TABLE basicdb_flowable.gen_table_column;
DROP TABLE basicdb_flowable.gen_table;
DROP TABLE basicdb_flowable.flw_ru_batch_part;
DROP TABLE basicdb_flowable.flw_ru_batch;
DROP TABLE basicdb_flowable.flw_expression;
DROP TABLE basicdb_flowable.flw_event_resource;
DROP TABLE basicdb_flowable.flw_event_deployment;
DROP TABLE basicdb_flowable.flw_event_definition;
DROP TABLE basicdb_flowable.flw_ev_databasechangeloglock;
DROP TABLE basicdb_flowable.flw_ev_databasechangelog;
DROP TABLE basicdb_flowable.flw_channel_definition;
DROP TABLE basicdb_flowable.act_ru_variable;
DROP TABLE basicdb_flowable.act_ru_timer_job;
DROP TABLE basicdb_flowable.act_ru_task;
DROP TABLE basicdb_flowable.act_ru_suspended_job;
DROP TABLE basicdb_flowable.act_ru_job;
DROP TABLE basicdb_flowable.act_ru_identitylink;
DROP TABLE basicdb_flowable.act_ru_history_job;
DROP TABLE basicdb_flowable.act_ru_external_job;
DROP TABLE basicdb_flowable.act_ru_execution;
DROP TABLE basicdb_flowable.act_ru_event_subscr;
DROP TABLE basicdb_flowable.act_ru_entitylink;
DROP TABLE basicdb_flowable.act_ru_deadletter_job;
DROP TABLE basicdb_flowable.act_ru_actinst;
DROP TABLE basicdb_flowable.act_re_procdef;
DROP TABLE basicdb_flowable.act_re_model;
DROP TABLE basicdb_flowable.act_re_deployment;
DROP TABLE basicdb_flowable.act_procdef_info;
DROP TABLE basicdb_flowable.act_hi_varinst;
DROP SEQUENCE basicdb_flowable.act_hi_tsk_log_id__seq;
DROP TABLE basicdb_flowable.act_hi_tsk_log;
DROP TABLE basicdb_flowable.act_hi_taskinst;
DROP TABLE basicdb_flowable.act_hi_procinst;
DROP TABLE basicdb_flowable.act_hi_identitylink;
DROP TABLE basicdb_flowable.act_hi_entitylink;
DROP TABLE basicdb_flowable.act_hi_detail;
DROP TABLE basicdb_flowable.act_hi_comment;
DROP TABLE basicdb_flowable.act_hi_attachment;
DROP TABLE basicdb_flowable.act_hi_actinst;
DROP TABLE basicdb_flowable.act_ge_property;
DROP TABLE basicdb_flowable.act_ge_bytearray;
DROP SEQUENCE basicdb_flowable.act_evt_log_log_nr__seq;
DROP TABLE basicdb_flowable.act_evt_log;
DROP TABLE basicdb_flowable.act_de_model_relation;
DROP TABLE basicdb_flowable.act_de_model_history;
DROP TABLE basicdb_flowable.act_de_model;
DROP FUNCTION basicdb_flowable.tabledef(oid);
DROP FUNCTION basicdb_flowable.ifnull(param1 text, param2 text);
DROP FUNCTION basicdb_flowable.find_in_set(bigint, character varying);
DROP FUNCTION basicdb_flowable.exec(sqlstring character varying);
DROP FUNCTION basicdb_flowable.date_format(indate anyelement, informate text);
DROP SCHEMA basicdb_flowable;
--
-- TOC entry 9 (class 2615 OID 4483622)
-- Name: basicdb_flowable; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA basicdb_flowable;


ALTER SCHEMA basicdb_flowable OWNER TO postgres;

--
-- TOC entry 392 (class 1255 OID 4483623)
-- Name: date_format(anyelement, text); Type: FUNCTION; Schema: basicdb_flowable; Owner: system
--

CREATE FUNCTION basicdb_flowable.date_format(indate anyelement, informate text) RETURNS text
    LANGUAGE plpgsql
    AS $_$
BEGIN
	IF upper($2) = upper('%Y%m%d') THEN
        return to_char($1,'YYYYMMDD');
    END IF;
	IF upper($2) = upper('%y%m%d') THEN
        return to_char($1,'YYMMDD');
    END IF;
    IF upper($2) = upper('%Y-%m-%d') THEN
        return to_char($1,'YYYY-MM-DD');
    END IF;
	
	IF upper($2) = upper('%Y-%m') THEN
        return to_char($1,'YYYY-MM');
    END IF;
	
	IF upper($2) = upper('%Y') THEN
        return to_char($1,'YYYY');
    END IF;
 
    IF upper($2) = upper('%Y-%m-%d %H-%i-%s') THEN
        return to_char($1,'YYYY-MM-DD HH24-MI-SS');
    END IF;
 
    IF upper($2) = upper('%Y-%m-%d %H-%i-%S') THEN
        return to_char($1,'YYYY-MM-DD HH24-MI-SS');
    END IF;
 
    IF upper($2) = upper('%Y-%m-%d %H-%i') THEN
        return to_char($1,'YYYY-MM-DD HH24-MI');
    END IF;
	
	IF upper($2) = upper('%Y-%m-%d %h-%i-%s') THEN
        return to_char($1,'YYYY-MM-DD HH-MI-SS');
    END IF;
 
    IF upper($2) = upper('%Y-%m-%d %h-%i-%S') THEN
        return to_char($1,'YYYY-MM-DD HH-MI-SS');
    END IF;
 
    IF upper($2) = upper('%Y-%m-%d %h-%i') THEN
        return to_char($1,'YYYY-MM-DD HH-MI');
    END IF;
 
    raise exception '时间格式:% 未在函数中定义，请添加！',$2;
 
    return null;
END;
$_$;


ALTER FUNCTION basicdb_flowable.date_format(indate anyelement, informate text) OWNER TO system;

--
-- TOC entry 393 (class 1255 OID 4483624)
-- Name: exec(character varying); Type: FUNCTION; Schema: basicdb_flowable; Owner: system
--

CREATE FUNCTION basicdb_flowable.exec(sqlstring character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
declare
res varchar(50);
BEGIN
EXECUTE sqlstring;
RETURN 'ok';
END
$$;


ALTER FUNCTION basicdb_flowable.exec(sqlstring character varying) OWNER TO system;

--
-- TOC entry 394 (class 1255 OID 4483625)
-- Name: find_in_set(bigint, character varying); Type: FUNCTION; Schema: basicdb_flowable; Owner: system
--

CREATE FUNCTION basicdb_flowable.find_in_set(bigint, character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $_$
DECLARE
    STR ALIAS FOR $1;
    STRS ALIAS FOR $2;
    POS INTEGER;
    STATUS BOOLEAN;
BEGIN
    SELECT POSITION( ','||STR||',' IN ','||STRS||',') INTO POS;
    IF POS > 0 THEN
        STATUS = TRUE;
    ELSE
        STATUS = FALSE;
    END IF;
    RETURN STATUS;
END;
$_$;


ALTER FUNCTION basicdb_flowable.find_in_set(bigint, character varying) OWNER TO system;

--
-- TOC entry 395 (class 1255 OID 4483626)
-- Name: ifnull(text, text); Type: FUNCTION; Schema: basicdb_flowable; Owner: system
--

CREATE FUNCTION basicdb_flowable.ifnull(param1 text, param2 text) RETURNS text
    LANGUAGE plpgsql
    AS $_$
BEGIN
    return COALESCE($1,$2);
END;
$_$;


ALTER FUNCTION basicdb_flowable.ifnull(param1 text, param2 text) OWNER TO system;

--
-- TOC entry 396 (class 1255 OID 4483627)
-- Name: tabledef(oid); Type: FUNCTION; Schema: basicdb_flowable; Owner: system
--

CREATE FUNCTION basicdb_flowable.tabledef(oid) RETURNS text
    LANGUAGE sql STRICT
    AS $_$
/* snatched from https://github.com/filiprem/pg-tools */
WITH attrdef AS (
    SELECT
        n.nspname,
        c.relname,
        pg_catalog.array_to_string(c.reloptions || array(select 'toast.' || x from pg_catalog.unnest(tc.reloptions) x), ', ') as relopts,
        c.relpersistence,
        a.attnum,
        a.attname,
        pg_catalog.format_type(a.atttypid, a.atttypmod) as atttype,
        (SELECT substring(pg_catalog.pg_get_expr(d.adbin, d.adrelid, true) for 128) FROM pg_catalog.pg_attrdef d
            WHERE d.adrelid = a.attrelid AND d.adnum = a.attnum AND a.atthasdef) as attdefault,
        a.attnotnull,
        (SELECT c.collname FROM pg_catalog.pg_collation c, pg_catalog.pg_type t
            WHERE c.oid = a.attcollation AND t.oid = a.atttypid AND a.attcollation <> t.typcollation) as attcollation,
        a.attidentity,
        a.attgenerated
    FROM pg_catalog.pg_attribute a
    JOIN pg_catalog.pg_class c ON a.attrelid = c.oid
    JOIN pg_catalog.pg_namespace n ON c.relnamespace = n.oid
    LEFT JOIN pg_catalog.pg_class tc ON (c.reltoastrelid = tc.oid)
    WHERE a.attrelid = $1
        AND a.attnum > 0
        AND NOT a.attisdropped
    ORDER BY a.attnum
),
coldef AS (
    SELECT
        attrdef.nspname,
        attrdef.relname,
        attrdef.relopts,
        attrdef.relpersistence,
        pg_catalog.format(
            '%I %s%s%s%s%s',
            attrdef.attname,
            attrdef.atttype,
            case when attrdef.attcollation is null then '' else pg_catalog.format(' COLLATE %I', attrdef.attcollation) end,
            case when attrdef.attnotnull then ' NOT NULL' else '' end,
            case when attrdef.attdefault is null then ''
                else case when attrdef.attgenerated = 's' then pg_catalog.format(' GENERATED ALWAYS AS (%s) STORED', attrdef.attdefault)
                    when attrdef.attgenerated <> '' then ' GENERATED AS NOT_IMPLEMENTED'
                       else ''
                end
            end,
            case when attrdef.attidentity<>'' then pg_catalog.format(' GENERATED %s AS IDENTITY',
                    case attrdef.attidentity when 'd' then 'BY DEFAULT' when 'a' then 'ALWAYS' else 'NOT_IMPLEMENTED' end)
                else '' end
        ) as col_create_sql
    FROM attrdef
    ORDER BY attrdef.attnum
),
tabdef AS (
    SELECT
        coldef.nspname,
        coldef.relname,
        coldef.relopts,
        coldef.relpersistence,
        string_agg(coldef.col_create_sql, E',\n    ') as cols_create_sql
    FROM coldef
    GROUP BY
        coldef.nspname, coldef.relname, coldef.relopts, coldef.relpersistence
)
SELECT
    format(
        'CREATE%s TABLE %I.%I%s%s%s;',
        case tabdef.relpersistence when 't' then ' TEMP' when 'u' then ' UNLOGGED' else '' end,
        tabdef.nspname,
        tabdef.relname,
        coalesce(
            (SELECT format(E'\n    PARTITION OF %I.%I %s\n', pn.nspname, pc.relname,
                pg_get_expr(c.relpartbound, c.oid))
                FROM pg_class c JOIN pg_inherits i ON c.oid = i.inhrelid
                JOIN pg_class pc ON pc.oid = i.inhparent
                JOIN pg_namespace pn ON pn.oid = pc.relnamespace
                WHERE c.oid = $1),
            format(E' (\n    %s\n)', tabdef.cols_create_sql)
        ),
        case when tabdef.relopts <> '' then format(' WITH (%s)', tabdef.relopts) else '' end,
        coalesce(E'\nPARTITION BY '||pg_get_partkeydef($1), '')
    ) as table_create_sql
FROM tabdef
$_$;


ALTER FUNCTION basicdb_flowable.tabledef(oid) OWNER TO system;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 369 (class 1259 OID 4485001)
-- Name: act_de_model; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_de_model (
    id character varying(255) NOT NULL,
    name character varying(255),
    model_key character varying(255),
    description character varying(4000),
    model_comment character varying(4000),
    created timestamp without time zone,
    created_by character varying(255),
    last_updated timestamp without time zone,
    last_updated_by character varying(255),
    version integer,
    model_editor_json text,
    model_type integer,
    thumbnail bytea,
    tenant_id character varying(255)
);


ALTER TABLE basicdb_flowable.act_de_model OWNER TO system;

--
-- TOC entry 370 (class 1259 OID 4485011)
-- Name: act_de_model_history; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_de_model_history (
    id character varying(255) NOT NULL,
    name character varying(255),
    model_key character varying(255),
    description character varying(4000),
    model_comment character varying(4000),
    created timestamp without time zone,
    created_by character varying(255),
    last_updated timestamp without time zone,
    last_updated_by character varying(255),
    version integer,
    model_editor_json text,
    model_type integer,
    model_id character varying(255),
    removal_date timestamp without time zone,
    tenant_id character varying(255)
);


ALTER TABLE basicdb_flowable.act_de_model_history OWNER TO system;

--
-- TOC entry 371 (class 1259 OID 4485019)
-- Name: act_de_model_relation; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_de_model_relation (
    id character varying(255) NOT NULL,
    parent_model_id character varying(255),
    model_id character varying(255),
    relation_type character varying(255)
);


ALTER TABLE basicdb_flowable.act_de_model_relation OWNER TO system;

--
-- TOC entry 361 (class 1259 OID 4484468)
-- Name: act_evt_log; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_evt_log (
    log_nr_ integer NOT NULL,
    type_ character varying(64),
    proc_def_id_ character varying(64),
    proc_inst_id_ character varying(64),
    execution_id_ character varying(64),
    task_id_ character varying(64),
    time_stamp_ timestamp without time zone NOT NULL,
    user_id_ character varying(255),
    data_ bytea,
    lock_owner_ character varying(255),
    lock_time_ timestamp without time zone,
    is_processed_ smallint DEFAULT 0
);


ALTER TABLE basicdb_flowable.act_evt_log OWNER TO system;

--
-- TOC entry 360 (class 1259 OID 4484467)
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: system
--

CREATE SEQUENCE basicdb_flowable.act_evt_log_log_nr__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.act_evt_log_log_nr__seq OWNER TO system;

--
-- TOC entry 4502 (class 0 OID 0)
-- Dependencies: 360
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: system
--

ALTER SEQUENCE basicdb_flowable.act_evt_log_log_nr__seq OWNED BY basicdb_flowable.act_evt_log.log_nr_;


--
-- TOC entry 330 (class 1259 OID 4484120)
-- Name: act_ge_bytearray; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ge_bytearray (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    deployment_id_ character varying(64),
    bytes_ bytea,
    generated_ boolean
);


ALTER TABLE basicdb_flowable.act_ge_bytearray OWNER TO system;

--
-- TOC entry 329 (class 1259 OID 4484115)
-- Name: act_ge_property; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ge_property (
    name_ character varying(64) NOT NULL,
    value_ character varying(300),
    rev_ integer
);


ALTER TABLE basicdb_flowable.act_ge_property OWNER TO system;

--
-- TOC entry 365 (class 1259 OID 4484702)
-- Name: act_hi_actinst; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_hi_actinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_def_id_ character varying(64) NOT NULL,
    proc_inst_id_ character varying(64) NOT NULL,
    execution_id_ character varying(64) NOT NULL,
    act_id_ character varying(255) NOT NULL,
    task_id_ character varying(64),
    call_proc_inst_id_ character varying(64),
    act_name_ character varying(255),
    act_type_ character varying(255) NOT NULL,
    assignee_ character varying(255),
    start_time_ timestamp without time zone NOT NULL,
    end_time_ timestamp without time zone,
    transaction_order_ integer,
    duration_ bigint,
    delete_reason_ character varying(4000),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.act_hi_actinst OWNER TO system;

--
-- TOC entry 368 (class 1259 OID 4484725)
-- Name: act_hi_attachment; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_hi_attachment (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    user_id_ character varying(255),
    name_ character varying(255),
    description_ character varying(4000),
    type_ character varying(255),
    task_id_ character varying(64),
    proc_inst_id_ character varying(64),
    url_ character varying(4000),
    content_id_ character varying(64),
    time_ timestamp without time zone
);


ALTER TABLE basicdb_flowable.act_hi_attachment OWNER TO system;

--
-- TOC entry 367 (class 1259 OID 4484718)
-- Name: act_hi_comment; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_hi_comment (
    id_ character varying(64) NOT NULL,
    type_ character varying(255),
    time_ timestamp without time zone NOT NULL,
    user_id_ character varying(255),
    task_id_ character varying(64),
    proc_inst_id_ character varying(64),
    action_ character varying(255),
    message_ character varying(4000),
    full_msg_ bytea
);


ALTER TABLE basicdb_flowable.act_hi_comment OWNER TO system;

--
-- TOC entry 366 (class 1259 OID 4484711)
-- Name: act_hi_detail; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_hi_detail (
    id_ character varying(64) NOT NULL,
    type_ character varying(255) NOT NULL,
    proc_inst_id_ character varying(64),
    execution_id_ character varying(64),
    task_id_ character varying(64),
    act_inst_id_ character varying(64),
    name_ character varying(255) NOT NULL,
    var_type_ character varying(64),
    rev_ integer,
    time_ timestamp without time zone NOT NULL,
    bytearray_id_ character varying(64),
    double_ double precision,
    long_ bigint,
    text_ character varying(4000),
    text2_ character varying(4000)
);


ALTER TABLE basicdb_flowable.act_hi_detail OWNER TO system;

--
-- TOC entry 340 (class 1259 OID 4484201)
-- Name: act_hi_entitylink; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_hi_entitylink (
    id_ character varying(64) NOT NULL,
    link_type_ character varying(255),
    create_time_ timestamp without time zone,
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    parent_element_id_ character varying(255),
    ref_scope_id_ character varying(255),
    ref_scope_type_ character varying(255),
    ref_scope_definition_id_ character varying(255),
    root_scope_id_ character varying(255),
    root_scope_type_ character varying(255),
    hierarchy_type_ character varying(255)
);


ALTER TABLE basicdb_flowable.act_hi_entitylink OWNER TO system;

--
-- TOC entry 338 (class 1259 OID 4484179)
-- Name: act_hi_identitylink; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_hi_identitylink (
    id_ character varying(64) NOT NULL,
    group_id_ character varying(255),
    type_ character varying(255),
    user_id_ character varying(255),
    task_id_ character varying(64),
    create_time_ timestamp without time zone,
    proc_inst_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255)
);


ALTER TABLE basicdb_flowable.act_hi_identitylink OWNER TO system;

--
-- TOC entry 364 (class 1259 OID 4484691)
-- Name: act_hi_procinst; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_hi_procinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_inst_id_ character varying(64) NOT NULL,
    business_key_ character varying(255),
    proc_def_id_ character varying(64) NOT NULL,
    start_time_ timestamp without time zone NOT NULL,
    end_time_ timestamp without time zone,
    duration_ bigint,
    start_user_id_ character varying(255),
    start_act_id_ character varying(255),
    end_act_id_ character varying(255),
    super_process_instance_id_ character varying(64),
    delete_reason_ character varying(4000),
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    name_ character varying(255),
    callback_id_ character varying(255),
    callback_type_ character varying(255),
    reference_id_ character varying(255),
    reference_type_ character varying(255),
    propagated_stage_inst_id_ character varying(255),
    business_status_ character varying(255)
);


ALTER TABLE basicdb_flowable.act_hi_procinst OWNER TO system;

--
-- TOC entry 343 (class 1259 OID 4484235)
-- Name: act_hi_taskinst; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_hi_taskinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_def_id_ character varying(64),
    task_def_id_ character varying(64),
    task_def_key_ character varying(255),
    proc_inst_id_ character varying(64),
    execution_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    propagated_stage_inst_id_ character varying(255),
    name_ character varying(255),
    parent_task_id_ character varying(64),
    description_ character varying(4000),
    owner_ character varying(255),
    assignee_ character varying(255),
    start_time_ timestamp without time zone NOT NULL,
    claim_time_ timestamp without time zone,
    end_time_ timestamp without time zone,
    duration_ bigint,
    delete_reason_ character varying(4000),
    priority_ integer,
    due_date_ timestamp without time zone,
    form_key_ character varying(255),
    category_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    last_updated_time_ timestamp without time zone
);


ALTER TABLE basicdb_flowable.act_hi_taskinst OWNER TO system;

--
-- TOC entry 345 (class 1259 OID 4484245)
-- Name: act_hi_tsk_log; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_hi_tsk_log (
    id_ integer NOT NULL,
    type_ character varying(64),
    task_id_ character varying(64) NOT NULL,
    time_stamp_ timestamp without time zone NOT NULL,
    user_id_ character varying(255),
    data_ character varying(4000),
    execution_id_ character varying(64),
    proc_inst_id_ character varying(64),
    proc_def_id_ character varying(64),
    scope_id_ character varying(255),
    scope_definition_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.act_hi_tsk_log OWNER TO system;

--
-- TOC entry 344 (class 1259 OID 4484244)
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: system
--

CREATE SEQUENCE basicdb_flowable.act_hi_tsk_log_id__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.act_hi_tsk_log_id__seq OWNER TO system;

--
-- TOC entry 4503 (class 0 OID 0)
-- Dependencies: 344
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: system
--

ALTER SEQUENCE basicdb_flowable.act_hi_tsk_log_id__seq OWNED BY basicdb_flowable.act_hi_tsk_log.id_;


--
-- TOC entry 347 (class 1259 OID 4484272)
-- Name: act_hi_varinst; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_hi_varinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_inst_id_ character varying(64),
    execution_id_ character varying(64),
    task_id_ character varying(64),
    name_ character varying(255) NOT NULL,
    var_type_ character varying(100),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    bytearray_id_ character varying(64),
    double_ double precision,
    long_ bigint,
    text_ character varying(4000),
    text2_ character varying(4000),
    create_time_ timestamp without time zone,
    last_updated_time_ timestamp without time zone
);


ALTER TABLE basicdb_flowable.act_hi_varinst OWNER TO system;

--
-- TOC entry 362 (class 1259 OID 4484477)
-- Name: act_procdef_info; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_procdef_info (
    id_ character varying(64) NOT NULL,
    proc_def_id_ character varying(64) NOT NULL,
    rev_ integer,
    info_json_id_ character varying(64)
);


ALTER TABLE basicdb_flowable.act_procdef_info OWNER TO system;

--
-- TOC entry 356 (class 1259 OID 4484434)
-- Name: act_re_deployment; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_re_deployment (
    id_ character varying(64) NOT NULL,
    name_ character varying(255),
    category_ character varying(255),
    key_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    deploy_time_ timestamp without time zone,
    derived_from_ character varying(64),
    derived_from_root_ character varying(64),
    parent_deployment_id_ character varying(255),
    engine_version_ character varying(255)
);


ALTER TABLE basicdb_flowable.act_re_deployment OWNER TO system;

--
-- TOC entry 357 (class 1259 OID 4484442)
-- Name: act_re_model; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_re_model (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    key_ character varying(255),
    category_ character varying(255),
    create_time_ timestamp without time zone,
    last_update_time_ timestamp without time zone,
    version_ integer,
    meta_info_ character varying(4000),
    deployment_id_ character varying(64),
    editor_source_value_id_ character varying(64),
    editor_source_extra_value_id_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.act_re_model OWNER TO system;

--
-- TOC entry 359 (class 1259 OID 4484458)
-- Name: act_re_procdef; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_re_procdef (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    name_ character varying(255),
    key_ character varying(255) NOT NULL,
    version_ integer NOT NULL,
    deployment_id_ character varying(64),
    resource_name_ character varying(4000),
    dgrm_resource_name_ character varying(4000),
    description_ character varying(4000),
    has_start_form_key_ boolean,
    has_graphical_notation_ boolean,
    suspension_state_ integer,
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    derived_from_ character varying(64),
    derived_from_root_ character varying(64),
    derived_version_ integer DEFAULT 0 NOT NULL,
    engine_version_ character varying(255)
);


ALTER TABLE basicdb_flowable.act_re_procdef OWNER TO system;

--
-- TOC entry 363 (class 1259 OID 4484482)
-- Name: act_ru_actinst; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_actinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_def_id_ character varying(64) NOT NULL,
    proc_inst_id_ character varying(64) NOT NULL,
    execution_id_ character varying(64) NOT NULL,
    act_id_ character varying(255) NOT NULL,
    task_id_ character varying(64),
    call_proc_inst_id_ character varying(64),
    act_name_ character varying(255),
    act_type_ character varying(255) NOT NULL,
    assignee_ character varying(255),
    start_time_ timestamp without time zone NOT NULL,
    end_time_ timestamp without time zone,
    duration_ bigint,
    transaction_order_ integer,
    delete_reason_ character varying(4000),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.act_ru_actinst OWNER TO system;

--
-- TOC entry 351 (class 1259 OID 4484307)
-- Name: act_ru_deadletter_job; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_deadletter_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.act_ru_deadletter_job OWNER TO system;

--
-- TOC entry 339 (class 1259 OID 4484190)
-- Name: act_ru_entitylink; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_entitylink (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    create_time_ timestamp without time zone,
    link_type_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    parent_element_id_ character varying(255),
    ref_scope_id_ character varying(255),
    ref_scope_type_ character varying(255),
    ref_scope_definition_id_ character varying(255),
    root_scope_id_ character varying(255),
    root_scope_type_ character varying(255),
    hierarchy_type_ character varying(255)
);


ALTER TABLE basicdb_flowable.act_ru_entitylink OWNER TO system;

--
-- TOC entry 341 (class 1259 OID 4484212)
-- Name: act_ru_event_subscr; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_event_subscr (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    event_type_ character varying(255) NOT NULL,
    event_name_ character varying(255),
    execution_id_ character varying(64),
    proc_inst_id_ character varying(64),
    activity_id_ character varying(64),
    configuration_ character varying(255),
    created_ timestamp without time zone NOT NULL,
    proc_def_id_ character varying(64),
    sub_scope_id_ character varying(64),
    scope_id_ character varying(64),
    scope_definition_id_ character varying(64),
    scope_type_ character varying(64),
    lock_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.act_ru_event_subscr OWNER TO system;

--
-- TOC entry 358 (class 1259 OID 4484450)
-- Name: act_ru_execution; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_execution (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    proc_inst_id_ character varying(64),
    business_key_ character varying(255),
    parent_id_ character varying(64),
    proc_def_id_ character varying(64),
    super_exec_ character varying(64),
    root_proc_inst_id_ character varying(64),
    act_id_ character varying(255),
    is_active_ boolean,
    is_concurrent_ boolean,
    is_scope_ boolean,
    is_event_scope_ boolean,
    is_mi_root_ boolean,
    suspension_state_ integer,
    cached_ent_state_ integer,
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    name_ character varying(255),
    start_act_id_ character varying(255),
    start_time_ timestamp without time zone,
    start_user_id_ character varying(255),
    lock_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    is_count_enabled_ boolean,
    evt_subscr_count_ integer,
    task_count_ integer,
    job_count_ integer,
    timer_job_count_ integer,
    susp_job_count_ integer,
    deadletter_job_count_ integer,
    external_worker_job_count_ integer,
    var_count_ integer,
    id_link_count_ integer,
    callback_id_ character varying(255),
    callback_type_ character varying(255),
    reference_id_ character varying(255),
    reference_type_ character varying(255),
    propagated_stage_inst_id_ character varying(255),
    business_status_ character varying(255)
);


ALTER TABLE basicdb_flowable.act_ru_execution OWNER TO system;

--
-- TOC entry 353 (class 1259 OID 4484323)
-- Name: act_ru_external_job; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_external_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    lock_exp_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.act_ru_external_job OWNER TO system;

--
-- TOC entry 352 (class 1259 OID 4484315)
-- Name: act_ru_history_job; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_history_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    lock_exp_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    adv_handler_cfg_id_ character varying(64),
    create_time_ timestamp without time zone,
    scope_type_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.act_ru_history_job OWNER TO system;

--
-- TOC entry 337 (class 1259 OID 4484167)
-- Name: act_ru_identitylink; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_identitylink (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    group_id_ character varying(255),
    type_ character varying(255),
    user_id_ character varying(255),
    task_id_ character varying(64),
    proc_inst_id_ character varying(64),
    proc_def_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255)
);


ALTER TABLE basicdb_flowable.act_ru_identitylink OWNER TO system;

--
-- TOC entry 348 (class 1259 OID 4484283)
-- Name: act_ru_job; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    lock_exp_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.act_ru_job OWNER TO system;

--
-- TOC entry 350 (class 1259 OID 4484299)
-- Name: act_ru_suspended_job; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_suspended_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.act_ru_suspended_job OWNER TO system;

--
-- TOC entry 342 (class 1259 OID 4484223)
-- Name: act_ru_task; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_task (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    execution_id_ character varying(64),
    proc_inst_id_ character varying(64),
    proc_def_id_ character varying(64),
    task_def_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    propagated_stage_inst_id_ character varying(255),
    name_ character varying(255),
    parent_task_id_ character varying(64),
    description_ character varying(4000),
    task_def_key_ character varying(255),
    owner_ character varying(255),
    assignee_ character varying(255),
    delegation_ character varying(64),
    priority_ integer,
    create_time_ timestamp without time zone,
    due_date_ timestamp without time zone,
    category_ character varying(255),
    suspension_state_ integer,
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    form_key_ character varying(255),
    claim_time_ timestamp without time zone,
    is_count_enabled_ boolean,
    var_count_ integer,
    id_link_count_ integer,
    sub_task_count_ integer
);


ALTER TABLE basicdb_flowable.act_ru_task OWNER TO system;

--
-- TOC entry 349 (class 1259 OID 4484291)
-- Name: act_ru_timer_job; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_timer_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    lock_exp_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.act_ru_timer_job OWNER TO system;

--
-- TOC entry 346 (class 1259 OID 4484257)
-- Name: act_ru_variable; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.act_ru_variable (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    type_ character varying(255) NOT NULL,
    name_ character varying(255) NOT NULL,
    execution_id_ character varying(64),
    proc_inst_id_ character varying(64),
    task_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    bytearray_id_ character varying(64),
    double_ double precision,
    long_ bigint,
    text_ character varying(4000),
    text2_ character varying(4000)
);


ALTER TABLE basicdb_flowable.act_ru_variable OWNER TO system;

--
-- TOC entry 336 (class 1259 OID 4484159)
-- Name: flw_channel_definition; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.flw_channel_definition (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    version_ integer,
    key_ character varying(255),
    category_ character varying(255),
    deployment_id_ character varying(255),
    create_time_ timestamp(3) without time zone,
    tenant_id_ character varying(255),
    resource_name_ character varying(255),
    description_ character varying(255),
    type_ character varying(255),
    implementation_ character varying(255)
);


ALTER TABLE basicdb_flowable.flw_channel_definition OWNER TO system;

--
-- TOC entry 332 (class 1259 OID 4484132)
-- Name: flw_ev_databasechangelog; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.flw_ev_databasechangelog (
    id character varying(255) NOT NULL,
    author character varying(255) NOT NULL,
    filename character varying(255) NOT NULL,
    dateexecuted timestamp without time zone NOT NULL,
    orderexecuted integer NOT NULL,
    exectype character varying(10) NOT NULL,
    md5sum character varying(35),
    description character varying(255),
    comments character varying(255),
    tag character varying(255),
    liquibase character varying(20),
    contexts character varying(255),
    labels character varying(255),
    deployment_id character varying(10)
);


ALTER TABLE basicdb_flowable.flw_ev_databasechangelog OWNER TO system;

--
-- TOC entry 331 (class 1259 OID 4484127)
-- Name: flw_ev_databasechangeloglock; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.flw_ev_databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp without time zone,
    lockedby character varying(255)
);


ALTER TABLE basicdb_flowable.flw_ev_databasechangeloglock OWNER TO system;

--
-- TOC entry 335 (class 1259 OID 4484151)
-- Name: flw_event_definition; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.flw_event_definition (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    version_ integer,
    key_ character varying(255),
    category_ character varying(255),
    deployment_id_ character varying(255),
    tenant_id_ character varying(255),
    resource_name_ character varying(255),
    description_ character varying(255)
);


ALTER TABLE basicdb_flowable.flw_event_definition OWNER TO system;

--
-- TOC entry 333 (class 1259 OID 4484137)
-- Name: flw_event_deployment; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.flw_event_deployment (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    category_ character varying(255),
    deploy_time_ timestamp(3) without time zone,
    tenant_id_ character varying(255),
    parent_deployment_id_ character varying(255)
);


ALTER TABLE basicdb_flowable.flw_event_deployment OWNER TO system;

--
-- TOC entry 334 (class 1259 OID 4484144)
-- Name: flw_event_resource; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.flw_event_resource (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    deployment_id_ character varying(255),
    resource_bytes_ bytea
);


ALTER TABLE basicdb_flowable.flw_event_resource OWNER TO system;

--
-- TOC entry 326 (class 1259 OID 4484052)
-- Name: flw_expression; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.flw_expression (
    id bigint NOT NULL,
    name character varying(64) DEFAULT NULL::character varying,
    expression text,
    status integer DEFAULT 0,
    type character varying(32) DEFAULT NULL::character varying,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE basicdb_flowable.flw_expression OWNER TO system;

--
-- TOC entry 4504 (class 0 OID 0)
-- Dependencies: 326
-- Name: TABLE flw_expression; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON TABLE basicdb_flowable.flw_expression IS '流程表达式字典';


--
-- TOC entry 4505 (class 0 OID 0)
-- Dependencies: 326
-- Name: COLUMN flw_expression.id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.flw_expression.id IS '主键';


--
-- TOC entry 4506 (class 0 OID 0)
-- Dependencies: 326
-- Name: COLUMN flw_expression.name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.flw_expression.name IS '表达式名称';


--
-- TOC entry 4507 (class 0 OID 0)
-- Dependencies: 326
-- Name: COLUMN flw_expression.expression; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.flw_expression.expression IS '表达式内容';


--
-- TOC entry 4508 (class 0 OID 0)
-- Dependencies: 326
-- Name: COLUMN flw_expression.status; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.flw_expression.status IS '状态（0启用 1停用）';


--
-- TOC entry 4509 (class 0 OID 0)
-- Dependencies: 326
-- Name: COLUMN flw_expression.type; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.flw_expression.type IS '类型';


--
-- TOC entry 4510 (class 0 OID 0)
-- Dependencies: 326
-- Name: COLUMN flw_expression.del_flag; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.flw_expression.del_flag IS '删除标志';


--
-- TOC entry 4511 (class 0 OID 0)
-- Dependencies: 326
-- Name: COLUMN flw_expression.create_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.flw_expression.create_by IS '创建者';


--
-- TOC entry 4512 (class 0 OID 0)
-- Dependencies: 326
-- Name: COLUMN flw_expression.create_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.flw_expression.create_time IS '创建时间';


--
-- TOC entry 4513 (class 0 OID 0)
-- Dependencies: 326
-- Name: COLUMN flw_expression.update_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.flw_expression.update_by IS '更新者';


--
-- TOC entry 4514 (class 0 OID 0)
-- Dependencies: 326
-- Name: COLUMN flw_expression.update_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.flw_expression.update_time IS '更新时间';


--
-- TOC entry 354 (class 1259 OID 4484412)
-- Name: flw_ru_batch; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.flw_ru_batch (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    type_ character varying(64) NOT NULL,
    search_key_ character varying(255),
    search_key2_ character varying(255),
    create_time_ timestamp without time zone NOT NULL,
    complete_time_ timestamp without time zone,
    status_ character varying(255),
    batch_doc_id_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.flw_ru_batch OWNER TO system;

--
-- TOC entry 355 (class 1259 OID 4484420)
-- Name: flw_ru_batch_part; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.flw_ru_batch_part (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    batch_id_ character varying(64),
    type_ character varying(64) NOT NULL,
    scope_id_ character varying(64),
    sub_scope_id_ character varying(64),
    scope_type_ character varying(64),
    search_key_ character varying(255),
    search_key2_ character varying(255),
    create_time_ timestamp without time zone NOT NULL,
    complete_time_ timestamp without time zone,
    status_ character varying(255),
    result_doc_id_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable.flw_ru_batch_part OWNER TO system;

--
-- TOC entry 268 (class 1259 OID 4483628)
-- Name: gen_table; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.gen_table (
    table_id bigint NOT NULL,
    table_name character varying(200),
    table_comment character varying(500),
    sub_table_name character varying(64),
    sub_table_fk_name character varying(64),
    class_name character varying(100),
    tpl_category character varying(200) DEFAULT 'crud'::character varying,
    package_name character varying(100),
    module_name character varying(30),
    business_name character varying(30),
    function_name character varying(50),
    function_author character varying(50),
    gen_type character varying(3) DEFAULT '0'::character varying,
    gen_path character varying(200) DEFAULT '/'::character varying,
    options character varying(1000),
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable.gen_table OWNER TO system;

--
-- TOC entry 269 (class 1259 OID 4483636)
-- Name: gen_table_column; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.gen_table_column (
    column_id bigint NOT NULL,
    table_id character varying(64),
    column_name character varying(200),
    column_comment character varying(500),
    column_type character varying(100),
    java_type character varying(500),
    java_field character varying(200),
    is_pk character varying(3),
    is_increment character varying(3),
    is_required character varying(3),
    is_insert character varying(3),
    is_edit character varying(3),
    is_list character varying(3),
    is_query character varying(3),
    query_type character varying(200) DEFAULT 'EQ'::character varying,
    html_type character varying(200),
    dict_type character varying(200),
    sort integer,
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone
);


ALTER TABLE basicdb_flowable.gen_table_column OWNER TO postgres;

--
-- TOC entry 270 (class 1259 OID 4483642)
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.gen_table_column_column_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.gen_table_column_column_id_seq OWNER TO postgres;

--
-- TOC entry 4517 (class 0 OID 0)
-- Dependencies: 270
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.gen_table_column_column_id_seq OWNED BY basicdb_flowable.gen_table_column.column_id;


--
-- TOC entry 271 (class 1259 OID 4483643)
-- Name: gen_table_table_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: system
--

CREATE SEQUENCE basicdb_flowable.gen_table_table_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.gen_table_table_id_seq OWNER TO system;

--
-- TOC entry 4519 (class 0 OID 0)
-- Dependencies: 271
-- Name: gen_table_table_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: system
--

ALTER SEQUENCE basicdb_flowable.gen_table_table_id_seq OWNED BY basicdb_flowable.gen_table.table_id;


--
-- TOC entry 272 (class 1259 OID 4483644)
-- Name: list_column; Type: VIEW; Schema: basicdb_flowable; Owner: system
--

CREATE VIEW basicdb_flowable.list_column AS
 SELECT c.relname AS table_name,
    a.attname AS column_name,
    d.description AS column_comment,
        CASE
            WHEN (a.attnotnull AND (con.conname IS NULL)) THEN 1
            ELSE 0
        END AS is_required,
        CASE
            WHEN (con.conname IS NOT NULL) THEN 1
            ELSE 0
        END AS is_pk,
    a.attnum AS sort,
        CASE
            WHEN ("position"(pg_get_expr(ad.adbin, ad.adrelid), ((((c.relname)::text || '_'::text) || (a.attname)::text) || '_seq'::text)) > 0) THEN 1
            ELSE 0
        END AS is_increment,
    btrim(
        CASE
            WHEN ((t.typelem <> (0)::oid) AND (t.typlen = '-1'::integer)) THEN 'ARRAY'::text
            ELSE
            CASE
                WHEN (t.typtype = 'd'::"char") THEN format_type(t.typbasetype, NULL::integer)
                ELSE format_type(a.atttypid, NULL::integer)
            END
        END, '"'::text) AS column_type
   FROM (((((pg_attribute a
     JOIN (pg_class c
     JOIN pg_namespace n ON ((c.relnamespace = n.oid))) ON ((a.attrelid = c.oid)))
     LEFT JOIN pg_description d ON (((d.objoid = c.oid) AND (a.attnum = d.objsubid))))
     LEFT JOIN pg_constraint con ON (((con.conrelid = c.oid) AND (a.attnum = ANY (con.conkey)))))
     LEFT JOIN pg_attrdef ad ON (((a.attrelid = ad.adrelid) AND (a.attnum = ad.adnum))))
     LEFT JOIN pg_type t ON ((a.atttypid = t.oid)))
  WHERE ((c.relkind = ANY (ARRAY['r'::"char", 'p'::"char"])) AND (a.attnum > 0) AND (n.nspname = 'basicdb_flowable'::name))
  ORDER BY c.relname, a.attnum;


ALTER VIEW basicdb_flowable.list_column OWNER TO system;

--
-- TOC entry 273 (class 1259 OID 4483649)
-- Name: list_table; Type: VIEW; Schema: basicdb_flowable; Owner: system
--

CREATE VIEW basicdb_flowable.list_table AS
 SELECT c.relname AS table_name,
    obj_description(c.oid) AS table_comment,
    CURRENT_TIMESTAMP AS create_time,
    CURRENT_TIMESTAMP AS update_time
   FROM (pg_class c
     LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace)))
  WHERE ((c.relkind = ANY (ARRAY['r'::"char", 'p'::"char"])) AND (c.relname !~~ 'spatial_%'::text) AND (n.nspname = 'basicdb_flowable'::name) AND (n.nspname <> ''::name));


ALTER VIEW basicdb_flowable.list_table OWNER TO system;

--
-- TOC entry 274 (class 1259 OID 4483654)
-- Name: qrtz_blob_triggers; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.qrtz_blob_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea
);


ALTER TABLE basicdb_flowable.qrtz_blob_triggers OWNER TO postgres;

--
-- TOC entry 275 (class 1259 OID 4483659)
-- Name: qrtz_calendars; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.qrtz_calendars (
    sched_name character varying(120) NOT NULL,
    calendar_name character varying(200) NOT NULL,
    calendar bytea NOT NULL
);


ALTER TABLE basicdb_flowable.qrtz_calendars OWNER TO postgres;

--
-- TOC entry 276 (class 1259 OID 4483664)
-- Name: qrtz_cron_triggers; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.qrtz_cron_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(200) NOT NULL,
    time_zone_id character varying(80)
);


ALTER TABLE basicdb_flowable.qrtz_cron_triggers OWNER TO postgres;

--
-- TOC entry 277 (class 1259 OID 4483669)
-- Name: qrtz_fired_triggers; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.qrtz_fired_triggers (
    sched_name character varying(120) NOT NULL,
    entry_id character varying(95) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    instance_name character varying(200) NOT NULL,
    fired_time bigint NOT NULL,
    sched_time bigint NOT NULL,
    priority integer NOT NULL,
    state character varying(16) NOT NULL,
    job_name character varying(200),
    job_group character varying(200),
    is_nonconcurrent character varying(1),
    requests_recovery character varying(1)
);


ALTER TABLE basicdb_flowable.qrtz_fired_triggers OWNER TO postgres;

--
-- TOC entry 278 (class 1259 OID 4483674)
-- Name: qrtz_job_details; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.qrtz_job_details (
    sched_name character varying(120) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    job_class_name character varying(250) NOT NULL,
    is_durable character varying(1) NOT NULL,
    is_nonconcurrent character varying(1) NOT NULL,
    is_update_data character varying(1) NOT NULL,
    requests_recovery character varying(1) NOT NULL,
    job_data bytea
);


ALTER TABLE basicdb_flowable.qrtz_job_details OWNER TO postgres;

--
-- TOC entry 279 (class 1259 OID 4483679)
-- Name: qrtz_locks; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.qrtz_locks (
    sched_name character varying(120) NOT NULL,
    lock_name character varying(40) NOT NULL
);


ALTER TABLE basicdb_flowable.qrtz_locks OWNER TO postgres;

--
-- TOC entry 280 (class 1259 OID 4483682)
-- Name: qrtz_paused_trigger_grps; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.qrtz_paused_trigger_grps (
    sched_name character varying(120) NOT NULL,
    trigger_group character varying(200) NOT NULL
);


ALTER TABLE basicdb_flowable.qrtz_paused_trigger_grps OWNER TO postgres;

--
-- TOC entry 281 (class 1259 OID 4483685)
-- Name: qrtz_scheduler_state; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.qrtz_scheduler_state (
    sched_name character varying(120) NOT NULL,
    instance_name character varying(200) NOT NULL,
    last_checkin_time bigint NOT NULL,
    checkin_interval bigint NOT NULL
);


ALTER TABLE basicdb_flowable.qrtz_scheduler_state OWNER TO postgres;

--
-- TOC entry 282 (class 1259 OID 4483688)
-- Name: qrtz_simple_triggers; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.qrtz_simple_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count bigint NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL
);


ALTER TABLE basicdb_flowable.qrtz_simple_triggers OWNER TO postgres;

--
-- TOC entry 283 (class 1259 OID 4483693)
-- Name: qrtz_simprop_triggers; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.qrtz_simprop_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    str_prop_1 character varying(512),
    str_prop_2 character varying(512),
    str_prop_3 character varying(512),
    int_prop_1 integer,
    int_prop_2 integer,
    long_prop_1 bigint,
    long_prop_2 bigint,
    dec_prop_1 numeric(13,4),
    dec_prop_2 numeric(13,4),
    bool_prop_1 character varying(1),
    bool_prop_2 character varying(1)
);


ALTER TABLE basicdb_flowable.qrtz_simprop_triggers OWNER TO postgres;

--
-- TOC entry 284 (class 1259 OID 4483698)
-- Name: qrtz_triggers; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.qrtz_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority integer,
    trigger_state character varying(16) NOT NULL,
    trigger_type character varying(8) NOT NULL,
    start_time bigint NOT NULL,
    end_time bigint,
    calendar_name character varying(200),
    misfire_instr smallint,
    job_data bytea
);


ALTER TABLE basicdb_flowable.qrtz_triggers OWNER TO postgres;

--
-- TOC entry 285 (class 1259 OID 4483703)
-- Name: sys_config; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_config (
    config_id integer NOT NULL,
    config_name character varying(100),
    config_key character varying(100),
    config_value character varying(500),
    config_type character varying(3) DEFAULT 'N'::character varying,
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable.sys_config OWNER TO postgres;

--
-- TOC entry 286 (class 1259 OID 4483709)
-- Name: sys_config_config_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_config_config_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_config_config_id_seq OWNER TO postgres;

--
-- TOC entry 4535 (class 0 OID 0)
-- Dependencies: 286
-- Name: sys_config_config_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_config_config_id_seq OWNED BY basicdb_flowable.sys_config.config_id;


--
-- TOC entry 287 (class 1259 OID 4483710)
-- Name: sys_demo; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.sys_demo (
    id bigint NOT NULL,
    name character varying(64),
    create_by character varying(32),
    create_time timestamp without time zone,
    create_id bigint,
    update_by character varying(32),
    update_time timestamp without time zone,
    update_id bigint,
    remark character varying(256),
    is_del character varying(1),
    sex character varying(8),
    birth timestamp without time zone
);


ALTER TABLE basicdb_flowable.sys_demo OWNER TO system;

--
-- TOC entry 288 (class 1259 OID 4483713)
-- Name: sys_dept; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_dept (
    dept_id bigint NOT NULL,
    parent_id bigint DEFAULT 0,
    ancestors character varying(50),
    dept_name character varying(30),
    order_num integer DEFAULT 0,
    leader character varying(20),
    phone character varying(11),
    email character varying(50),
    status character varying(3) DEFAULT '0'::character varying,
    del_flag character varying(3) DEFAULT '0'::character varying,
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone,
    type character varying(64),
    remark text,
    identifier character varying(128)
);


ALTER TABLE basicdb_flowable.sys_dept OWNER TO postgres;

--
-- TOC entry 289 (class 1259 OID 4483722)
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_dept_dept_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_dept_dept_id_seq OWNER TO postgres;

--
-- TOC entry 4538 (class 0 OID 0)
-- Dependencies: 289
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_dept_dept_id_seq OWNED BY basicdb_flowable.sys_dept.dept_id;


--
-- TOC entry 290 (class 1259 OID 4483723)
-- Name: sys_dict_data; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_dict_data (
    dict_code bigint NOT NULL,
    dict_sort integer DEFAULT 0,
    dict_label character varying(100),
    dict_value character varying(100),
    dict_type character varying(100),
    css_class character varying(100),
    list_class character varying(100),
    is_default character varying(3) DEFAULT 'N'::character varying,
    status character varying(3) DEFAULT '0'::character varying,
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable.sys_dict_data OWNER TO postgres;

--
-- TOC entry 291 (class 1259 OID 4483731)
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_dict_data_dict_code_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_dict_data_dict_code_seq OWNER TO postgres;

--
-- TOC entry 4541 (class 0 OID 0)
-- Dependencies: 291
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_dict_data_dict_code_seq OWNED BY basicdb_flowable.sys_dict_data.dict_code;


--
-- TOC entry 292 (class 1259 OID 4483732)
-- Name: sys_dict_type; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_dict_type (
    dict_id bigint NOT NULL,
    dict_name character varying(100),
    dict_type character varying(100),
    status character varying(3) DEFAULT '0'::character varying,
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable.sys_dict_type OWNER TO postgres;

--
-- TOC entry 293 (class 1259 OID 4483738)
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_dict_type_dict_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_dict_type_dict_id_seq OWNER TO postgres;

--
-- TOC entry 4544 (class 0 OID 0)
-- Dependencies: 293
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_dict_type_dict_id_seq OWNED BY basicdb_flowable.sys_dict_type.dict_id;


--
-- TOC entry 294 (class 1259 OID 4483739)
-- Name: sys_files; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.sys_files (
    id bigint NOT NULL,
    name character varying(128),
    absolute_path character varying(256),
    relative_path character varying(256),
    size double precision,
    identifier character varying(32),
    remark text,
    create_time timestamp(0) without time zone,
    create_by character varying(32),
    is_del character varying(1),
    create_id bigint,
    update_id bigint,
    update_by character varying(32),
    update_time timestamp(0) without time zone,
    task_id character varying(64) DEFAULT NULL::character varying,
    process_instance_id character varying(64) DEFAULT NULL::character varying,
    user_id bigint,
    nick_name character varying(64) DEFAULT NULL::character varying,
    activity_id character varying(64) DEFAULT NULL::character varying,
    url character varying(512) DEFAULT NULL::character varying,
    upload_type character varying(2) DEFAULT '2'::character varying
);


ALTER TABLE basicdb_flowable.sys_files OWNER TO system;

--
-- TOC entry 4546 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN sys_files.task_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.sys_files.task_id IS '任务ID';


--
-- TOC entry 4547 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN sys_files.process_instance_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.sys_files.process_instance_id IS '流程实例ID';


--
-- TOC entry 4548 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN sys_files.user_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.sys_files.user_id IS '上传人ID';


--
-- TOC entry 4549 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN sys_files.nick_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.sys_files.nick_name IS '上传人姓名';


--
-- TOC entry 4550 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN sys_files.activity_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.sys_files.activity_id IS '流程节点ID';


--
-- TOC entry 4551 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN sys_files.url; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.sys_files.url IS '访问URL';


--
-- TOC entry 4552 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN sys_files.upload_type; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.sys_files.upload_type IS '上传类型：1 受限 2 公开';


--
-- TOC entry 295 (class 1259 OID 4483744)
-- Name: sys_i18n; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.sys_i18n (
    message_id character varying(255) NOT NULL,
    value text NOT NULL,
    language character varying(50) NOT NULL
);


ALTER TABLE basicdb_flowable.sys_i18n OWNER TO system;

--
-- TOC entry 4553 (class 0 OID 0)
-- Dependencies: 295
-- Name: COLUMN sys_i18n.message_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.sys_i18n.message_id IS '标识';


--
-- TOC entry 4554 (class 0 OID 0)
-- Dependencies: 295
-- Name: COLUMN sys_i18n.value; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.sys_i18n.value IS '值';


--
-- TOC entry 4555 (class 0 OID 0)
-- Dependencies: 295
-- Name: COLUMN sys_i18n.language; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.sys_i18n.language IS '语言';


--
-- TOC entry 296 (class 1259 OID 4483749)
-- Name: sys_job; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_job (
    job_id bigint NOT NULL,
    job_name character varying(64) NOT NULL,
    job_group character varying(64) DEFAULT 'DEFAULT'::character varying NOT NULL,
    invoke_target character varying(500) NOT NULL,
    cron_expression character varying(255),
    misfire_policy character varying(20) DEFAULT '3'::character varying,
    concurrent character varying(3) DEFAULT '1'::character varying,
    status character varying(3) DEFAULT '0'::character varying,
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable.sys_job OWNER TO postgres;

--
-- TOC entry 297 (class 1259 OID 4483758)
-- Name: sys_job_job_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_job_job_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_job_job_id_seq OWNER TO postgres;

--
-- TOC entry 4557 (class 0 OID 0)
-- Dependencies: 297
-- Name: sys_job_job_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_job_job_id_seq OWNED BY basicdb_flowable.sys_job.job_id;


--
-- TOC entry 298 (class 1259 OID 4483759)
-- Name: sys_job_log; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_job_log (
    job_log_id bigint NOT NULL,
    job_name character varying(64) NOT NULL,
    job_group character varying(64) NOT NULL,
    invoke_target character varying(500) NOT NULL,
    job_message character varying(500),
    status character varying(3) DEFAULT '0'::character varying,
    exception_info character varying(2000),
    create_time timestamp(0) without time zone
);


ALTER TABLE basicdb_flowable.sys_job_log OWNER TO postgres;

--
-- TOC entry 299 (class 1259 OID 4483765)
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_job_log_job_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_job_log_job_log_id_seq OWNER TO postgres;

--
-- TOC entry 4560 (class 0 OID 0)
-- Dependencies: 299
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_job_log_job_log_id_seq OWNED BY basicdb_flowable.sys_job_log.job_log_id;


--
-- TOC entry 300 (class 1259 OID 4483766)
-- Name: sys_logininfor; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_logininfor (
    info_id bigint NOT NULL,
    user_name character varying(50),
    ipaddr character varying(128),
    login_location character varying(255),
    browser character varying(50),
    os character varying(50),
    status character varying(3) DEFAULT '0'::character varying,
    msg character varying(255),
    login_time timestamp(0) without time zone
);


ALTER TABLE basicdb_flowable.sys_logininfor OWNER TO postgres;

--
-- TOC entry 301 (class 1259 OID 4483772)
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_logininfor_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_logininfor_info_id_seq OWNER TO postgres;

--
-- TOC entry 4563 (class 0 OID 0)
-- Dependencies: 301
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_logininfor_info_id_seq OWNED BY basicdb_flowable.sys_logininfor.info_id;


--
-- TOC entry 302 (class 1259 OID 4483773)
-- Name: sys_menu; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_menu (
    menu_id bigint NOT NULL,
    menu_name character varying(50) NOT NULL,
    parent_id bigint DEFAULT 0,
    order_num integer DEFAULT 0,
    path character varying(200),
    component character varying(255),
    query character varying(255),
    is_frame character varying(1) DEFAULT 1,
    is_cache character varying(1) DEFAULT 0,
    menu_type character varying(3),
    visible character varying(3) DEFAULT '0'::character varying,
    status character varying(3) DEFAULT '0'::character varying,
    perms character varying(100),
    icon character varying(100) DEFAULT '#'::character varying,
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone,
    remark character varying(500),
    is_resource character varying(1),
    show_tags character varying(5),
    menu_name_en character varying(50),
    menu_name_zh_hant character varying(50)
);


ALTER TABLE basicdb_flowable.sys_menu OWNER TO postgres;

--
-- TOC entry 4565 (class 0 OID 0)
-- Dependencies: 302
-- Name: COLUMN sys_menu.show_tags; Type: COMMENT; Schema: basicdb_flowable; Owner: postgres
--

COMMENT ON COLUMN basicdb_flowable.sys_menu.show_tags IS '自定义配置该页面是否展示Tags-view';


--
-- TOC entry 303 (class 1259 OID 4483785)
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_menu_menu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_menu_menu_id_seq OWNER TO postgres;

--
-- TOC entry 4567 (class 0 OID 0)
-- Dependencies: 303
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_menu_menu_id_seq OWNED BY basicdb_flowable.sys_menu.menu_id;


--
-- TOC entry 304 (class 1259 OID 4483786)
-- Name: sys_notice; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_notice (
    notice_id bigint NOT NULL,
    notice_title character varying(50) NOT NULL,
    notice_type character varying(3) NOT NULL,
    notice_content bytea,
    status character varying(3) DEFAULT '0'::character varying,
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone,
    remark character varying(255)
);


ALTER TABLE basicdb_flowable.sys_notice OWNER TO postgres;

--
-- TOC entry 305 (class 1259 OID 4483792)
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_notice_notice_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_notice_notice_id_seq OWNER TO postgres;

--
-- TOC entry 4570 (class 0 OID 0)
-- Dependencies: 305
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_notice_notice_id_seq OWNED BY basicdb_flowable.sys_notice.notice_id;


--
-- TOC entry 372 (class 1259 OID 4487127)
-- Name: sys_notice_read; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.sys_notice_read (
    read_id bigint NOT NULL,
    notice_id bigint NOT NULL,
    user_id bigint NOT NULL,
    read_time timestamp(0) without time zone
);


ALTER TABLE basicdb_flowable.sys_notice_read OWNER TO system;

--
-- TOC entry 373 (class 1259 OID 4487130)
-- Name: sys_notice_read_read_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: system
--

CREATE SEQUENCE basicdb_flowable.sys_notice_read_read_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_notice_read_read_id_seq OWNER TO system;

--
-- TOC entry 4572 (class 0 OID 0)
-- Dependencies: 373
-- Name: sys_notice_read_read_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: system
--

ALTER SEQUENCE basicdb_flowable.sys_notice_read_read_id_seq OWNED BY basicdb_flowable.sys_notice_read.read_id;


--
-- TOC entry 306 (class 1259 OID 4483793)
-- Name: sys_oper_log; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_oper_log (
    oper_id bigint NOT NULL,
    title character varying(50),
    business_type integer DEFAULT 0,
    method character varying(100),
    request_method character varying(10),
    operator_type integer DEFAULT 0,
    oper_name character varying(50),
    dept_name character varying(50),
    oper_url character varying(255),
    oper_ip character varying(128),
    oper_location character varying(255),
    oper_param character varying(2000),
    json_result character varying(2000),
    status integer DEFAULT 0,
    error_msg character varying(2000),
    oper_time timestamp(0) without time zone
);


ALTER TABLE basicdb_flowable.sys_oper_log OWNER TO postgres;

--
-- TOC entry 307 (class 1259 OID 4483801)
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_oper_log_oper_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_oper_log_oper_id_seq OWNER TO postgres;

--
-- TOC entry 4574 (class 0 OID 0)
-- Dependencies: 307
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_oper_log_oper_id_seq OWNED BY basicdb_flowable.sys_oper_log.oper_id;


--
-- TOC entry 308 (class 1259 OID 4483802)
-- Name: sys_post; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_post (
    post_id bigint NOT NULL,
    post_code character varying(64) NOT NULL,
    post_name character varying(50) NOT NULL,
    post_sort integer NOT NULL,
    status character varying(3) NOT NULL,
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable.sys_post OWNER TO postgres;

--
-- TOC entry 309 (class 1259 OID 4483807)
-- Name: sys_post_post_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_post_post_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_post_post_id_seq OWNER TO postgres;

--
-- TOC entry 4577 (class 0 OID 0)
-- Dependencies: 309
-- Name: sys_post_post_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_post_post_id_seq OWNED BY basicdb_flowable.sys_post.post_id;


--
-- TOC entry 310 (class 1259 OID 4483808)
-- Name: sys_role; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_role (
    role_id bigint NOT NULL,
    role_name character varying(30) NOT NULL,
    role_key character varying(100) NOT NULL,
    role_sort integer NOT NULL,
    data_scope character varying(3) DEFAULT '1'::character varying,
    menu_check_strictly boolean DEFAULT true,
    dept_check_strictly boolean DEFAULT true,
    status character varying(3) NOT NULL,
    del_flag character varying(3) DEFAULT '0'::character varying,
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable.sys_role OWNER TO postgres;

--
-- TOC entry 4579 (class 0 OID 0)
-- Dependencies: 310
-- Name: COLUMN sys_role.menu_check_strictly; Type: COMMENT; Schema: basicdb_flowable; Owner: postgres
--

COMMENT ON COLUMN basicdb_flowable.sys_role.menu_check_strictly IS '菜单树选择项是否父子互相关联';


--
-- TOC entry 4580 (class 0 OID 0)
-- Dependencies: 310
-- Name: COLUMN sys_role.dept_check_strictly; Type: COMMENT; Schema: basicdb_flowable; Owner: postgres
--

COMMENT ON COLUMN basicdb_flowable.sys_role.dept_check_strictly IS '部门树选择项是否父子互相关联';


--
-- TOC entry 311 (class 1259 OID 4483817)
-- Name: sys_role_dept; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_role_dept (
    role_id bigint NOT NULL,
    dept_id bigint NOT NULL
);


ALTER TABLE basicdb_flowable.sys_role_dept OWNER TO postgres;

--
-- TOC entry 312 (class 1259 OID 4483820)
-- Name: sys_role_menu; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_role_menu (
    role_id bigint NOT NULL,
    menu_id bigint NOT NULL
);


ALTER TABLE basicdb_flowable.sys_role_menu OWNER TO postgres;

--
-- TOC entry 313 (class 1259 OID 4483823)
-- Name: sys_role_role_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_role_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_role_role_id_seq OWNER TO postgres;

--
-- TOC entry 4584 (class 0 OID 0)
-- Dependencies: 313
-- Name: sys_role_role_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_role_role_id_seq OWNED BY basicdb_flowable.sys_role.role_id;


--
-- TOC entry 314 (class 1259 OID 4483824)
-- Name: sys_user; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_user (
    user_id bigint NOT NULL,
    dept_id bigint,
    user_name character varying(30) NOT NULL,
    nick_name character varying(30) NOT NULL,
    user_type character varying(2) DEFAULT '00'::character varying,
    email character varying(50),
    phonenumber character varying(11),
    sex character varying(3) DEFAULT '0'::character varying,
    avatar character varying(100),
    password character varying(100),
    status character varying(3) DEFAULT '0'::character varying,
    del_flag character varying(3) DEFAULT '0'::character varying,
    login_ip character varying(128),
    login_date timestamp(0) without time zone,
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone,
    remark character varying(500),
    change_password_time timestamp(0) without time zone
);


ALTER TABLE basicdb_flowable.sys_user OWNER TO postgres;

--
-- TOC entry 315 (class 1259 OID 4483833)
-- Name: sys_user_post; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_user_post (
    user_id bigint NOT NULL,
    post_id bigint NOT NULL
);


ALTER TABLE basicdb_flowable.sys_user_post OWNER TO postgres;

--
-- TOC entry 316 (class 1259 OID 4483836)
-- Name: sys_user_role; Type: TABLE; Schema: basicdb_flowable; Owner: postgres
--

CREATE TABLE basicdb_flowable.sys_user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE basicdb_flowable.sys_user_role OWNER TO postgres;

--
-- TOC entry 317 (class 1259 OID 4483839)
-- Name: sys_user_user_id_seq; Type: SEQUENCE; Schema: basicdb_flowable; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable.sys_user_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable.sys_user_user_id_seq OWNER TO postgres;

--
-- TOC entry 4589 (class 0 OID 0)
-- Dependencies: 317
-- Name: sys_user_user_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable.sys_user_user_id_seq OWNED BY basicdb_flowable.sys_user.user_id;


--
-- TOC entry 327 (class 1259 OID 4484065)
-- Name: tbl_flow_extend_procinst; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.tbl_flow_extend_procinst (
    id character varying(64) NOT NULL,
    proc_ins_id character varying(64) DEFAULT NULL::character varying,
    proc_def_id character varying(64) DEFAULT NULL::character varying,
    model_key character varying(64) DEFAULT NULL::character varying,
    model_id character varying(64) DEFAULT NULL::character varying,
    process_name character varying(255) DEFAULT NULL::character varying,
    process_status character varying(20) DEFAULT NULL::character varying,
    current_user_code character varying(64) DEFAULT NULL::character varying,
    tenant_id character varying(32) DEFAULT NULL::character varying,
    form_data text,
    current_name character varying(64) DEFAULT NULL::character varying,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone
);


ALTER TABLE basicdb_flowable.tbl_flow_extend_procinst OWNER TO system;

--
-- TOC entry 4591 (class 0 OID 0)
-- Dependencies: 327
-- Name: TABLE tbl_flow_extend_procinst; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON TABLE basicdb_flowable.tbl_flow_extend_procinst IS '流程实例业务拓展';


--
-- TOC entry 4592 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.id IS '主键';


--
-- TOC entry 4593 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.proc_ins_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.proc_ins_id IS '流程实例ID';


--
-- TOC entry 4594 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.proc_def_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.proc_def_id IS '流程定义ID';


--
-- TOC entry 4595 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.model_key; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.model_key IS '模型Key';


--
-- TOC entry 4596 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.model_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.model_id IS '模型ID';


--
-- TOC entry 4597 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.process_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.process_name IS '流程名称';


--
-- TOC entry 4598 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.process_status; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.process_status IS '流程状态';


--
-- TOC entry 4599 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.current_user_code; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.current_user_code IS '当前处理人编码';


--
-- TOC entry 4600 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.tenant_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.tenant_id IS '租户ID';


--
-- TOC entry 4601 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.form_data; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.form_data IS '表单数据JSON';


--
-- TOC entry 4602 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.current_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.current_name IS '当前处理人姓名';


--
-- TOC entry 4603 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.del_flag; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.del_flag IS '删除标志';


--
-- TOC entry 4604 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.create_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.create_by IS '创建者';


--
-- TOC entry 4605 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.create_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.create_time IS '创建时间';


--
-- TOC entry 4606 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.update_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.update_by IS '更新者';


--
-- TOC entry 4607 (class 0 OID 0)
-- Dependencies: 327
-- Name: COLUMN tbl_flow_extend_procinst.update_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.tbl_flow_extend_procinst.update_time IS '更新时间';


--
-- TOC entry 318 (class 1259 OID 4483921)
-- Name: wf_category; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.wf_category (
    category_id bigint NOT NULL,
    category_name character varying(64) DEFAULT NULL::character varying,
    code character varying(64) DEFAULT NULL::character varying,
    parent bigint DEFAULT 0,
    url character varying(255) DEFAULT NULL::character varying,
    remark character varying(500) DEFAULT NULL::character varying,
    financeagency_id bigint,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone
);


ALTER TABLE basicdb_flowable.wf_category OWNER TO system;

--
-- TOC entry 4608 (class 0 OID 0)
-- Dependencies: 318
-- Name: TABLE wf_category; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON TABLE basicdb_flowable.wf_category IS '流程分类';


--
-- TOC entry 4609 (class 0 OID 0)
-- Dependencies: 318
-- Name: COLUMN wf_category.category_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category.category_id IS '分类ID';


--
-- TOC entry 4610 (class 0 OID 0)
-- Dependencies: 318
-- Name: COLUMN wf_category.category_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category.category_name IS '分类名称';


--
-- TOC entry 4611 (class 0 OID 0)
-- Dependencies: 318
-- Name: COLUMN wf_category.code; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category.code IS '分类编码';


--
-- TOC entry 4612 (class 0 OID 0)
-- Dependencies: 318
-- Name: COLUMN wf_category.parent; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category.parent IS '父级分类ID';


--
-- TOC entry 4613 (class 0 OID 0)
-- Dependencies: 318
-- Name: COLUMN wf_category.url; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category.url IS '跳转地址（业务表单）';


--
-- TOC entry 4614 (class 0 OID 0)
-- Dependencies: 318
-- Name: COLUMN wf_category.remark; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category.remark IS '备注';


--
-- TOC entry 4615 (class 0 OID 0)
-- Dependencies: 318
-- Name: COLUMN wf_category.financeagency_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category.financeagency_id IS '所属组织架构ID';


--
-- TOC entry 4616 (class 0 OID 0)
-- Dependencies: 318
-- Name: COLUMN wf_category.del_flag; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category.del_flag IS '删除标志（0存在 2删除）';


--
-- TOC entry 4617 (class 0 OID 0)
-- Dependencies: 318
-- Name: COLUMN wf_category.create_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category.create_by IS '创建者';


--
-- TOC entry 4618 (class 0 OID 0)
-- Dependencies: 318
-- Name: COLUMN wf_category.create_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category.create_time IS '创建时间';


--
-- TOC entry 4619 (class 0 OID 0)
-- Dependencies: 318
-- Name: COLUMN wf_category.update_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category.update_by IS '更新者';


--
-- TOC entry 4620 (class 0 OID 0)
-- Dependencies: 318
-- Name: COLUMN wf_category.update_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category.update_time IS '更新时间';


--
-- TOC entry 328 (class 1259 OID 4484092)
-- Name: wf_category_org_link; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.wf_category_org_link (
    id bigint NOT NULL,
    category_id bigint NOT NULL,
    org_id bigint NOT NULL,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone
);


ALTER TABLE basicdb_flowable.wf_category_org_link OWNER TO system;

--
-- TOC entry 4621 (class 0 OID 0)
-- Dependencies: 328
-- Name: TABLE wf_category_org_link; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON TABLE basicdb_flowable.wf_category_org_link IS '流程分类与组织关联';


--
-- TOC entry 4622 (class 0 OID 0)
-- Dependencies: 328
-- Name: COLUMN wf_category_org_link.id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category_org_link.id IS '主键';


--
-- TOC entry 4623 (class 0 OID 0)
-- Dependencies: 328
-- Name: COLUMN wf_category_org_link.category_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category_org_link.category_id IS '流程分类ID';


--
-- TOC entry 4624 (class 0 OID 0)
-- Dependencies: 328
-- Name: COLUMN wf_category_org_link.org_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category_org_link.org_id IS '组织ID';


--
-- TOC entry 4625 (class 0 OID 0)
-- Dependencies: 328
-- Name: COLUMN wf_category_org_link.create_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category_org_link.create_by IS '创建者';


--
-- TOC entry 4626 (class 0 OID 0)
-- Dependencies: 328
-- Name: COLUMN wf_category_org_link.create_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category_org_link.create_time IS '创建时间';


--
-- TOC entry 4627 (class 0 OID 0)
-- Dependencies: 328
-- Name: COLUMN wf_category_org_link.update_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category_org_link.update_by IS '更新者';


--
-- TOC entry 4628 (class 0 OID 0)
-- Dependencies: 328
-- Name: COLUMN wf_category_org_link.update_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_category_org_link.update_time IS '更新时间';


--
-- TOC entry 325 (class 1259 OID 4484030)
-- Name: wf_comment_info; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.wf_comment_info (
    id character varying(32) NOT NULL,
    type character varying(20) DEFAULT NULL::character varying,
    user_id character varying(64) DEFAULT NULL::character varying,
    user_name character varying(64) DEFAULT NULL::character varying,
    "time" timestamp without time zone,
    task_id character varying(64) DEFAULT NULL::character varying,
    activity_id character varying(64) DEFAULT NULL::character varying,
    activity_name character varying(64) DEFAULT NULL::character varying,
    activity_type character varying(32) DEFAULT NULL::character varying,
    proc_ins_id character varying(64) DEFAULT NULL::character varying,
    action character varying(20) DEFAULT NULL::character varying,
    message text,
    end_time timestamp without time zone,
    duration character varying(32) DEFAULT NULL::character varying,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE basicdb_flowable.wf_comment_info OWNER TO system;

--
-- TOC entry 4629 (class 0 OID 0)
-- Dependencies: 325
-- Name: TABLE wf_comment_info; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON TABLE basicdb_flowable.wf_comment_info IS '审批意见';


--
-- TOC entry 4630 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.id IS '主键';


--
-- TOC entry 4631 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.type; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.type IS '意见类型（1同意 2拒绝 3退回 4撤回）';


--
-- TOC entry 4632 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.user_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.user_id IS '处理人ID';


--
-- TOC entry 4633 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.user_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.user_name IS '处理人姓名';


--
-- TOC entry 4634 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info."time"; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info."time" IS '处理时间';


--
-- TOC entry 4635 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.task_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.task_id IS '任务ID';


--
-- TOC entry 4636 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.activity_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.activity_id IS '节点ID';


--
-- TOC entry 4637 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.activity_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.activity_name IS '节点名称';


--
-- TOC entry 4638 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.activity_type; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.activity_type IS '节点类型';


--
-- TOC entry 4639 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.proc_ins_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.proc_ins_id IS '流程实例ID';


--
-- TOC entry 4640 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.action; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.action IS '动作（通过/拒绝/驳回等）';


--
-- TOC entry 4641 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.message; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.message IS '审批意见';


--
-- TOC entry 4642 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.end_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.end_time IS '结束时间';


--
-- TOC entry 4643 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.duration; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.duration IS '耗时';


--
-- TOC entry 4644 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.del_flag; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.del_flag IS '删除标志';


--
-- TOC entry 4645 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.create_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.create_by IS '创建者';


--
-- TOC entry 4646 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.create_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.create_time IS '创建时间';


--
-- TOC entry 4647 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.update_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.update_by IS '更新者';


--
-- TOC entry 4648 (class 0 OID 0)
-- Dependencies: 325
-- Name: COLUMN wf_comment_info.update_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_comment_info.update_time IS '更新时间';


--
-- TOC entry 324 (class 1259 OID 4484009)
-- Name: wf_copy; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.wf_copy (
    copy_id bigint NOT NULL,
    title character varying(255) DEFAULT NULL::character varying,
    process_id character varying(64) DEFAULT NULL::character varying,
    process_name character varying(64) DEFAULT NULL::character varying,
    category_id character varying(32) DEFAULT NULL::character varying,
    deployment_id character varying(64) DEFAULT NULL::character varying,
    instance_id character varying(64) DEFAULT NULL::character varying,
    task_id character varying(64) DEFAULT NULL::character varying,
    user_id character varying(64) DEFAULT NULL::character varying,
    originator_id character varying(64) DEFAULT NULL::character varying,
    originator_name character varying(64) DEFAULT NULL::character varying,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    status character(1) DEFAULT '0'::bpchar,
    activity_id character varying(64) DEFAULT NULL::character varying,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE basicdb_flowable.wf_copy OWNER TO system;

--
-- TOC entry 4649 (class 0 OID 0)
-- Dependencies: 324
-- Name: TABLE wf_copy; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON TABLE basicdb_flowable.wf_copy IS '流程抄送';


--
-- TOC entry 4650 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.copy_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.copy_id IS '抄送ID';


--
-- TOC entry 4651 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.title; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.title IS '抄送标题';


--
-- TOC entry 4652 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.process_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.process_id IS '流程定义ID';


--
-- TOC entry 4653 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.process_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.process_name IS '流程名称';


--
-- TOC entry 4654 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.category_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.category_id IS '分类ID';


--
-- TOC entry 4655 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.deployment_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.deployment_id IS '部署ID';


--
-- TOC entry 4656 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.instance_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.instance_id IS '流程实例ID';


--
-- TOC entry 4657 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.task_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.task_id IS '任务ID';


--
-- TOC entry 4658 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.user_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.user_id IS '抄送接收人ID';


--
-- TOC entry 4659 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.originator_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.originator_id IS '发起人ID';


--
-- TOC entry 4660 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.originator_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.originator_name IS '发起人姓名';


--
-- TOC entry 4661 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.create_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.create_by IS '创建者';


--
-- TOC entry 4662 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.create_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.create_time IS '创建时间';


--
-- TOC entry 4663 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.update_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.update_by IS '更新者';


--
-- TOC entry 4664 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.update_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.update_time IS '更新时间';


--
-- TOC entry 4665 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.status; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.status IS '已读状态（0未读 1已读）';


--
-- TOC entry 4666 (class 0 OID 0)
-- Dependencies: 324
-- Name: COLUMN wf_copy.activity_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_copy.activity_id IS '节点id';


--
-- TOC entry 322 (class 1259 OID 4483975)
-- Name: wf_deploy_form; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.wf_deploy_form (
    id character varying(64) NOT NULL,
    deploy_id character varying(64) NOT NULL,
    form_id character varying(32) NOT NULL,
    activity_id character varying(64) DEFAULT NULL::character varying,
    activity_name character varying(64) DEFAULT NULL::character varying,
    activity_type character varying(32) DEFAULT NULL::character varying,
    model_key character varying(64) DEFAULT NULL::character varying,
    form_name character varying(64) DEFAULT NULL::character varying,
    content text,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone
);


ALTER TABLE basicdb_flowable.wf_deploy_form OWNER TO system;

--
-- TOC entry 4667 (class 0 OID 0)
-- Dependencies: 322
-- Name: TABLE wf_deploy_form; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON TABLE basicdb_flowable.wf_deploy_form IS '部署表单关联';


--
-- TOC entry 4668 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.id IS '主键';


--
-- TOC entry 4669 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.deploy_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.deploy_id IS '部署ID';


--
-- TOC entry 4670 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.form_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.form_id IS '表单ID';


--
-- TOC entry 4671 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.activity_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.activity_id IS '节点ID';


--
-- TOC entry 4672 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.activity_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.activity_name IS '节点名称';


--
-- TOC entry 4673 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.activity_type; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.activity_type IS '节点类型';


--
-- TOC entry 4674 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.model_key; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.model_key IS '模型Key';


--
-- TOC entry 4675 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.form_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.form_name IS '表单名称';


--
-- TOC entry 4676 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.content; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.content IS '表单内容';


--
-- TOC entry 4677 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.create_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.create_by IS '创建者';


--
-- TOC entry 4678 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.create_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.create_time IS '创建时间';


--
-- TOC entry 4679 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.update_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.update_by IS '更新者';


--
-- TOC entry 4680 (class 0 OID 0)
-- Dependencies: 322
-- Name: COLUMN wf_deploy_form.update_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_deploy_form.update_time IS '更新时间';


--
-- TOC entry 319 (class 1259 OID 4483936)
-- Name: wf_form; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.wf_form (
    form_id character varying(32) NOT NULL,
    form_name character varying(64) DEFAULT NULL::character varying,
    code character varying(64) DEFAULT NULL::character varying,
    content text,
    type character varying(20) DEFAULT NULL::character varying,
    category_id character varying(32) DEFAULT NULL::character varying,
    version integer DEFAULT 1,
    remark character varying(500) DEFAULT NULL::character varying,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    status character varying(10) DEFAULT '0'::character varying
);


ALTER TABLE basicdb_flowable.wf_form OWNER TO system;

--
-- TOC entry 4681 (class 0 OID 0)
-- Dependencies: 319
-- Name: TABLE wf_form; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON TABLE basicdb_flowable.wf_form IS '流程表单';


--
-- TOC entry 4682 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.form_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.form_id IS '表单ID';


--
-- TOC entry 4683 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.form_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.form_name IS '表单名称';


--
-- TOC entry 4684 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.code; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.code IS '表单编码（唯一）';


--
-- TOC entry 4685 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.content; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.content IS '表单JSON内容';


--
-- TOC entry 4686 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.type; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.type IS '表单类型（自定义/系统）';


--
-- TOC entry 4687 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.category_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.category_id IS '表单分类ID';


--
-- TOC entry 4688 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.version; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.version IS '版本号';


--
-- TOC entry 4689 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.remark; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.remark IS '备注';


--
-- TOC entry 4690 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.del_flag; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.del_flag IS '删除标志（0存在 2删除）';


--
-- TOC entry 4691 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.create_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.create_by IS '创建者';


--
-- TOC entry 4692 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.create_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.create_time IS '创建时间';


--
-- TOC entry 4693 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.update_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.update_by IS '更新者';


--
-- TOC entry 4694 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.update_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.update_time IS '更新时间';


--
-- TOC entry 4695 (class 0 OID 0)
-- Dependencies: 319
-- Name: COLUMN wf_form.status; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form.status IS '状态';


--
-- TOC entry 321 (class 1259 OID 4483964)
-- Name: wf_form_category; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.wf_form_category (
    id character varying(32) NOT NULL,
    category_name character varying(64) DEFAULT NULL::character varying,
    parent character varying(32) DEFAULT '0'::character varying,
    order_num integer DEFAULT 0,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE basicdb_flowable.wf_form_category OWNER TO system;

--
-- TOC entry 4696 (class 0 OID 0)
-- Dependencies: 321
-- Name: TABLE wf_form_category; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON TABLE basicdb_flowable.wf_form_category IS '流程表单分类';


--
-- TOC entry 4697 (class 0 OID 0)
-- Dependencies: 321
-- Name: COLUMN wf_form_category.id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_category.id IS '分类ID';


--
-- TOC entry 4698 (class 0 OID 0)
-- Dependencies: 321
-- Name: COLUMN wf_form_category.category_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_category.category_name IS '分类名称';


--
-- TOC entry 4699 (class 0 OID 0)
-- Dependencies: 321
-- Name: COLUMN wf_form_category.parent; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_category.parent IS '父级分类ID';


--
-- TOC entry 4700 (class 0 OID 0)
-- Dependencies: 321
-- Name: COLUMN wf_form_category.order_num; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_category.order_num IS '显示顺序';


--
-- TOC entry 4701 (class 0 OID 0)
-- Dependencies: 321
-- Name: COLUMN wf_form_category.del_flag; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_category.del_flag IS '删除标志（0存在 2删除）';


--
-- TOC entry 4702 (class 0 OID 0)
-- Dependencies: 321
-- Name: COLUMN wf_form_category.create_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_category.create_by IS '创建者';


--
-- TOC entry 4703 (class 0 OID 0)
-- Dependencies: 321
-- Name: COLUMN wf_form_category.create_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_category.create_time IS '创建时间';


--
-- TOC entry 4704 (class 0 OID 0)
-- Dependencies: 321
-- Name: COLUMN wf_form_category.update_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_category.update_by IS '更新者';


--
-- TOC entry 4705 (class 0 OID 0)
-- Dependencies: 321
-- Name: COLUMN wf_form_category.update_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_category.update_time IS '更新时间';


--
-- TOC entry 320 (class 1259 OID 4483952)
-- Name: wf_form_template; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.wf_form_template (
    form_id character varying(32) NOT NULL,
    form_name character varying(64) DEFAULT NULL::character varying,
    content text,
    img_url character varying(255) DEFAULT NULL::character varying,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE basicdb_flowable.wf_form_template OWNER TO system;

--
-- TOC entry 4706 (class 0 OID 0)
-- Dependencies: 320
-- Name: TABLE wf_form_template; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON TABLE basicdb_flowable.wf_form_template IS '流程表单模板';


--
-- TOC entry 4707 (class 0 OID 0)
-- Dependencies: 320
-- Name: COLUMN wf_form_template.form_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_template.form_id IS '表单ID';


--
-- TOC entry 4708 (class 0 OID 0)
-- Dependencies: 320
-- Name: COLUMN wf_form_template.form_name; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_template.form_name IS '模板名称';


--
-- TOC entry 4709 (class 0 OID 0)
-- Dependencies: 320
-- Name: COLUMN wf_form_template.content; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_template.content IS '模板JSON';


--
-- TOC entry 4710 (class 0 OID 0)
-- Dependencies: 320
-- Name: COLUMN wf_form_template.img_url; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_template.img_url IS '模板预览图URL';


--
-- TOC entry 4711 (class 0 OID 0)
-- Dependencies: 320
-- Name: COLUMN wf_form_template.del_flag; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_template.del_flag IS '删除标志（0存在 2删除）';


--
-- TOC entry 4712 (class 0 OID 0)
-- Dependencies: 320
-- Name: COLUMN wf_form_template.create_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_template.create_by IS '创建者';


--
-- TOC entry 4713 (class 0 OID 0)
-- Dependencies: 320
-- Name: COLUMN wf_form_template.create_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_template.create_time IS '创建时间';


--
-- TOC entry 4714 (class 0 OID 0)
-- Dependencies: 320
-- Name: COLUMN wf_form_template.update_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_template.update_by IS '更新者';


--
-- TOC entry 4715 (class 0 OID 0)
-- Dependencies: 320
-- Name: COLUMN wf_form_template.update_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_form_template.update_time IS '更新时间';


--
-- TOC entry 323 (class 1259 OID 4483991)
-- Name: wf_task_form; Type: TABLE; Schema: basicdb_flowable; Owner: system
--

CREATE TABLE basicdb_flowable.wf_task_form (
    id character varying(64) NOT NULL,
    form_id character varying(32) DEFAULT NULL::character varying,
    proc_ins_id character varying(64) DEFAULT NULL::character varying,
    task_id character varying(64) DEFAULT NULL::character varying,
    activity_id character varying(64) DEFAULT NULL::character varying,
    user_id character varying(64) DEFAULT NULL::character varying,
    form_content text,
    field character varying(255) DEFAULT NULL::character varying,
    field_value text,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone
);


ALTER TABLE basicdb_flowable.wf_task_form OWNER TO system;

--
-- TOC entry 4716 (class 0 OID 0)
-- Dependencies: 323
-- Name: TABLE wf_task_form; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON TABLE basicdb_flowable.wf_task_form IS '任务表单快照';


--
-- TOC entry 4717 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.id IS '主键';


--
-- TOC entry 4718 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.form_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.form_id IS '表单ID';


--
-- TOC entry 4719 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.proc_ins_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.proc_ins_id IS '流程实例ID';


--
-- TOC entry 4720 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.task_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.task_id IS '任务ID';


--
-- TOC entry 4721 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.activity_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.activity_id IS '节点ID';


--
-- TOC entry 4722 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.user_id; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.user_id IS '处理人ID';


--
-- TOC entry 4723 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.form_content; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.form_content IS '表单内容快照';


--
-- TOC entry 4724 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.field; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.field IS '字段';


--
-- TOC entry 4725 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.field_value; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.field_value IS '字段值';


--
-- TOC entry 4726 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.create_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.create_by IS '创建者';


--
-- TOC entry 4727 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.create_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.create_time IS '创建时间';


--
-- TOC entry 4728 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.update_by; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.update_by IS '更新者';


--
-- TOC entry 4729 (class 0 OID 0)
-- Dependencies: 323
-- Name: COLUMN wf_task_form.update_time; Type: COMMENT; Schema: basicdb_flowable; Owner: system
--

COMMENT ON COLUMN basicdb_flowable.wf_task_form.update_time IS '更新时间';


--
-- TOC entry 3874 (class 2604 OID 4484471)
-- Name: act_evt_log log_nr_; Type: DEFAULT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_evt_log ALTER COLUMN log_nr_ SET DEFAULT nextval('basicdb_flowable.act_evt_log_log_nr__seq'::regclass);


--
-- TOC entry 3858 (class 2604 OID 4484248)
-- Name: act_hi_tsk_log id_; Type: DEFAULT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_hi_tsk_log ALTER COLUMN id_ SET DEFAULT nextval('basicdb_flowable.act_hi_tsk_log_id__seq'::regclass);


--
-- TOC entry 3699 (class 2604 OID 4483840)
-- Name: gen_table table_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.gen_table ALTER COLUMN table_id SET DEFAULT nextval('basicdb_flowable.gen_table_table_id_seq'::regclass);


--
-- TOC entry 3703 (class 2604 OID 4483841)
-- Name: gen_table_column column_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.gen_table_column ALTER COLUMN column_id SET DEFAULT nextval('basicdb_flowable.gen_table_column_column_id_seq'::regclass);


--
-- TOC entry 3705 (class 2604 OID 4483842)
-- Name: sys_config config_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_config ALTER COLUMN config_id SET DEFAULT nextval('basicdb_flowable.sys_config_config_id_seq'::regclass);


--
-- TOC entry 3707 (class 2604 OID 4483843)
-- Name: sys_dept dept_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_dept ALTER COLUMN dept_id SET DEFAULT nextval('basicdb_flowable.sys_dept_dept_id_seq'::regclass);


--
-- TOC entry 3712 (class 2604 OID 4483844)
-- Name: sys_dict_data dict_code; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_dict_data ALTER COLUMN dict_code SET DEFAULT nextval('basicdb_flowable.sys_dict_data_dict_code_seq'::regclass);


--
-- TOC entry 3716 (class 2604 OID 4483845)
-- Name: sys_dict_type dict_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_dict_type ALTER COLUMN dict_id SET DEFAULT nextval('basicdb_flowable.sys_dict_type_dict_id_seq'::regclass);


--
-- TOC entry 3724 (class 2604 OID 4483846)
-- Name: sys_job job_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_job ALTER COLUMN job_id SET DEFAULT nextval('basicdb_flowable.sys_job_job_id_seq'::regclass);


--
-- TOC entry 3729 (class 2604 OID 4483847)
-- Name: sys_job_log job_log_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_job_log ALTER COLUMN job_log_id SET DEFAULT nextval('basicdb_flowable.sys_job_log_job_log_id_seq'::regclass);


--
-- TOC entry 3731 (class 2604 OID 4483848)
-- Name: sys_logininfor info_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_logininfor ALTER COLUMN info_id SET DEFAULT nextval('basicdb_flowable.sys_logininfor_info_id_seq'::regclass);


--
-- TOC entry 3733 (class 2604 OID 4483849)
-- Name: sys_menu menu_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_menu ALTER COLUMN menu_id SET DEFAULT nextval('basicdb_flowable.sys_menu_menu_id_seq'::regclass);


--
-- TOC entry 3741 (class 2604 OID 4483850)
-- Name: sys_notice notice_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_notice ALTER COLUMN notice_id SET DEFAULT nextval('basicdb_flowable.sys_notice_notice_id_seq'::regclass);


--
-- TOC entry 3743 (class 2604 OID 4483851)
-- Name: sys_oper_log oper_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_oper_log ALTER COLUMN oper_id SET DEFAULT nextval('basicdb_flowable.sys_oper_log_oper_id_seq'::regclass);


--
-- TOC entry 3747 (class 2604 OID 4483852)
-- Name: sys_post post_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_post ALTER COLUMN post_id SET DEFAULT nextval('basicdb_flowable.sys_post_post_id_seq'::regclass);


--
-- TOC entry 3748 (class 2604 OID 4483853)
-- Name: sys_role role_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_role ALTER COLUMN role_id SET DEFAULT nextval('basicdb_flowable.sys_role_role_id_seq'::regclass);


--
-- TOC entry 3753 (class 2604 OID 4483854)
-- Name: sys_user user_id; Type: DEFAULT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_user ALTER COLUMN user_id SET DEFAULT nextval('basicdb_flowable.sys_user_user_id_seq'::regclass);


--
-- TOC entry 4492 (class 0 OID 4485001)
-- Dependencies: 369
-- Data for Name: act_de_model; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4493 (class 0 OID 4485011)
-- Dependencies: 370
-- Data for Name: act_de_model_history; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4494 (class 0 OID 4485019)
-- Dependencies: 371
-- Data for Name: act_de_model_relation; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4484 (class 0 OID 4484468)
-- Dependencies: 361
-- Data for Name: act_evt_log; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4453 (class 0 OID 4484120)
-- Dependencies: 330
-- Data for Name: act_ge_bytearray; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4452 (class 0 OID 4484115)
-- Dependencies: 329
-- Data for Name: act_ge_property; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--

INSERT INTO basicdb_flowable.act_ge_property VALUES ('common.schema.version', '6.8.0.0', 1);
INSERT INTO basicdb_flowable.act_ge_property VALUES ('next.dbid', '1', 1);
INSERT INTO basicdb_flowable.act_ge_property VALUES ('identitylink.schema.version', '6.8.0.0', 1);
INSERT INTO basicdb_flowable.act_ge_property VALUES ('entitylink.schema.version', '6.8.0.0', 1);
INSERT INTO basicdb_flowable.act_ge_property VALUES ('eventsubscription.schema.version', '6.8.0.0', 1);
INSERT INTO basicdb_flowable.act_ge_property VALUES ('task.schema.version', '6.8.0.0', 1);
INSERT INTO basicdb_flowable.act_ge_property VALUES ('variable.schema.version', '6.8.0.0', 1);
INSERT INTO basicdb_flowable.act_ge_property VALUES ('job.schema.version', '6.8.0.0', 1);
INSERT INTO basicdb_flowable.act_ge_property VALUES ('batch.schema.version', '6.8.0.0', 1);
INSERT INTO basicdb_flowable.act_ge_property VALUES ('schema.version', '6.8.0.0', 1);
INSERT INTO basicdb_flowable.act_ge_property VALUES ('schema.history', 'create(6.8.0.0)', 1);
INSERT INTO basicdb_flowable.act_ge_property VALUES ('cfg.execution-related-entities-count', 'true', 1);
INSERT INTO basicdb_flowable.act_ge_property VALUES ('cfg.task-related-entities-count', 'true', 1);


--
-- TOC entry 4488 (class 0 OID 4484702)
-- Dependencies: 365
-- Data for Name: act_hi_actinst; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4491 (class 0 OID 4484725)
-- Dependencies: 368
-- Data for Name: act_hi_attachment; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4490 (class 0 OID 4484718)
-- Dependencies: 367
-- Data for Name: act_hi_comment; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4489 (class 0 OID 4484711)
-- Dependencies: 366
-- Data for Name: act_hi_detail; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4463 (class 0 OID 4484201)
-- Dependencies: 340
-- Data for Name: act_hi_entitylink; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4461 (class 0 OID 4484179)
-- Dependencies: 338
-- Data for Name: act_hi_identitylink; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4487 (class 0 OID 4484691)
-- Dependencies: 364
-- Data for Name: act_hi_procinst; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4466 (class 0 OID 4484235)
-- Dependencies: 343
-- Data for Name: act_hi_taskinst; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4468 (class 0 OID 4484245)
-- Dependencies: 345
-- Data for Name: act_hi_tsk_log; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4470 (class 0 OID 4484272)
-- Dependencies: 347
-- Data for Name: act_hi_varinst; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4485 (class 0 OID 4484477)
-- Dependencies: 362
-- Data for Name: act_procdef_info; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4479 (class 0 OID 4484434)
-- Dependencies: 356
-- Data for Name: act_re_deployment; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4480 (class 0 OID 4484442)
-- Dependencies: 357
-- Data for Name: act_re_model; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4482 (class 0 OID 4484458)
-- Dependencies: 359
-- Data for Name: act_re_procdef; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4486 (class 0 OID 4484482)
-- Dependencies: 363
-- Data for Name: act_ru_actinst; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4474 (class 0 OID 4484307)
-- Dependencies: 351
-- Data for Name: act_ru_deadletter_job; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4462 (class 0 OID 4484190)
-- Dependencies: 339
-- Data for Name: act_ru_entitylink; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4464 (class 0 OID 4484212)
-- Dependencies: 341
-- Data for Name: act_ru_event_subscr; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4481 (class 0 OID 4484450)
-- Dependencies: 358
-- Data for Name: act_ru_execution; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4476 (class 0 OID 4484323)
-- Dependencies: 353
-- Data for Name: act_ru_external_job; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4475 (class 0 OID 4484315)
-- Dependencies: 352
-- Data for Name: act_ru_history_job; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4460 (class 0 OID 4484167)
-- Dependencies: 337
-- Data for Name: act_ru_identitylink; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4471 (class 0 OID 4484283)
-- Dependencies: 348
-- Data for Name: act_ru_job; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4473 (class 0 OID 4484299)
-- Dependencies: 350
-- Data for Name: act_ru_suspended_job; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4465 (class 0 OID 4484223)
-- Dependencies: 342
-- Data for Name: act_ru_task; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4472 (class 0 OID 4484291)
-- Dependencies: 349
-- Data for Name: act_ru_timer_job; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4469 (class 0 OID 4484257)
-- Dependencies: 346
-- Data for Name: act_ru_variable; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4459 (class 0 OID 4484159)
-- Dependencies: 336
-- Data for Name: flw_channel_definition; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4455 (class 0 OID 4484132)
-- Dependencies: 332
-- Data for Name: flw_ev_databasechangelog; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--

INSERT INTO basicdb_flowable.flw_ev_databasechangelog VALUES ('1', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', '2026-10-14 16:20:15.06944', 1, 'EXECUTED', '8:1b0c48c9cf7945be799d868a2626d687', 'createTable tableName=FLW_EVENT_DEPLOYMENT; createTable tableName=FLW_EVENT_RESOURCE; createTable tableName=FLW_EVENT_DEFINITION; createIndex indexName=ACT_IDX_EVENT_DEF_UNIQ, tableName=FLW_EVENT_DEFINITION; createTable tableName=FLW_CHANNEL_DEFIN...', '', NULL, '4.3.5', NULL, NULL, '1966014534');
INSERT INTO basicdb_flowable.flw_ev_databasechangelog VALUES ('2', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', '2026-10-14 16:20:15.082258', 2, 'EXECUTED', '8:0ea825feb8e470558f0b5754352b9cda', 'addColumn tableName=FLW_CHANNEL_DEFINITION; addColumn tableName=FLW_CHANNEL_DEFINITION', '', NULL, '4.3.5', NULL, NULL, '1966014534');
INSERT INTO basicdb_flowable.flw_ev_databasechangelog VALUES ('3', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', '2026-10-14 16:20:15.102374', 3, 'EXECUTED', '8:3c2bb293350b5cbe6504331980c9dcee', 'customChange', '', NULL, '4.3.5', NULL, NULL, '1966014534');


--
-- TOC entry 4454 (class 0 OID 4484127)
-- Dependencies: 331
-- Data for Name: flw_ev_databasechangeloglock; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--

INSERT INTO basicdb_flowable.flw_ev_databasechangeloglock VALUES (1, false, NULL, NULL);


--
-- TOC entry 4458 (class 0 OID 4484151)
-- Dependencies: 335
-- Data for Name: flw_event_definition; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4456 (class 0 OID 4484137)
-- Dependencies: 333
-- Data for Name: flw_event_deployment; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4457 (class 0 OID 4484144)
-- Dependencies: 334
-- Data for Name: flw_event_resource; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4449 (class 0 OID 4484052)
-- Dependencies: 326
-- Data for Name: flw_expression; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4477 (class 0 OID 4484412)
-- Dependencies: 354
-- Data for Name: flw_ru_batch; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4478 (class 0 OID 4484420)
-- Dependencies: 355
-- Data for Name: flw_ru_batch_part; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4393 (class 0 OID 4483628)
-- Dependencies: 268
-- Data for Name: gen_table; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--

INSERT INTO basicdb_flowable.gen_table VALUES (1, 'sys_role_dept', NULL, NULL, NULL, 'SysRoleDept', 'crud', 'com.basic.system', 'system', 'dept', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (2, 'sys_role_menu', NULL, NULL, NULL, 'SysRoleMenu', 'crud', 'com.basic.system', 'system', 'menu', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (3, 'sys_user', NULL, NULL, NULL, 'SysUser', 'crud', 'com.basic.system', 'system', 'user', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (4, 'sys_user_post', NULL, NULL, NULL, 'SysUserPost', 'crud', 'com.basic.system', 'system', 'post', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (5, 'sys_user_role', NULL, NULL, NULL, 'SysUserRole', 'crud', 'com.basic.system', 'system', 'role', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (6, 'sys_notice', NULL, NULL, NULL, 'SysNotice', 'crud', 'com.basic.system', 'system', 'notice', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (7, 'sys_menu', NULL, NULL, NULL, 'SysMenu', 'crud', 'com.basic.system', 'system', 'menu', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (8, 'sys_config', NULL, NULL, NULL, 'SysConfig', 'crud', 'com.basic.system', 'system', 'config', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (9, 'sys_dept', NULL, NULL, NULL, 'SysDept', 'crud', 'com.basic.system', 'system', 'dept', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (10, 'sys_dict_data', NULL, NULL, NULL, 'SysDictData', 'crud', 'com.basic.system', 'system', 'data', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (11, 'sys_dict_type', NULL, NULL, NULL, 'SysDictType', 'crud', 'com.basic.system', 'system', 'type', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (12, 'sys_job', NULL, NULL, NULL, 'SysJob', 'crud', 'com.basic.system', 'system', 'job', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (13, 'sys_job_log', NULL, NULL, NULL, 'SysJobLog', 'crud', 'com.basic.system', 'system', 'log', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (14, 'sys_logininfor', NULL, NULL, NULL, 'SysLogininfor', 'crud', 'com.basic.system', 'system', 'logininfor', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (15, 'sys_oper_log', NULL, NULL, NULL, 'SysOperLog', 'crud', 'com.basic.system', 'system', 'log', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (16, 'sys_post', NULL, NULL, NULL, 'SysPost', 'crud', 'com.basic.system', 'system', 'post', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (17, 'sys_role', NULL, NULL, NULL, 'SysRole', 'crud', 'com.basic.system', 'system', 'role', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.gen_table VALUES (18, 'sys_demo', NULL, NULL, NULL, 'SysDemo', 'crud', 'com.basic.system', 'system', 'demo', NULL, 'basic', '0', '/', NULL, 'admin', '2024-02-01 11:09:28', NULL, NULL, NULL);


--
-- TOC entry 4394 (class 0 OID 4483636)
-- Dependencies: 269
-- Data for Name: gen_table_column; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.gen_table_column VALUES (3, '2', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (4, '2', 'menu_id', NULL, 'bigint', 'Long', 'menuId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (5, '3', 'user_id', NULL, 'bigint', 'Long', 'userId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (6, '3', 'dept_id', NULL, 'bigint', 'Long', 'deptId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (7, '3', 'user_name', NULL, 'character varying', 'String', 'userName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 3, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (8, '3', 'nick_name', NULL, 'character varying', 'String', 'nickName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 4, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (9, '3', 'user_type', NULL, 'character varying', 'String', 'userType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 5, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (10, '3', 'email', NULL, 'character varying', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (11, '3', 'phonenumber', NULL, 'character varying', 'String', 'phonenumber', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (12, '3', 'sex', NULL, 'character varying', 'String', 'sex', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 8, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (13, '3', 'avatar', NULL, 'character varying', 'String', 'avatar', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (14, '3', 'password', NULL, 'character varying', 'String', 'password', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (15, '3', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 11, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (16, '3', 'del_flag', NULL, 'character varying', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (17, '3', 'login_ip', NULL, 'character varying', 'String', 'loginIp', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (18, '3', 'login_date', NULL, 'timestamp without time zone', 'String', 'loginDate', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (19, '3', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 15, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (20, '3', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 16, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (21, '3', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 17, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (22, '3', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 18, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (23, '3', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 19, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (24, '4', 'user_id', NULL, 'bigint', 'Long', 'userId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (25, '4', 'post_id', NULL, 'bigint', 'Long', 'postId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (26, '5', 'user_id', NULL, 'bigint', 'Long', 'userId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (27, '5', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (28, '6', 'notice_id', NULL, 'bigint', 'Long', 'noticeId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (29, '6', 'notice_title', NULL, 'character varying', 'String', 'noticeTitle', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (30, '6', 'notice_type', NULL, 'character varying', 'String', 'noticeType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', NULL, 3, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (31, '6', 'notice_content', NULL, 'bytea', 'String', 'noticeContent', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'editor', NULL, 4, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (32, '6', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 5, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (33, '6', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (34, '6', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (35, '6', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (36, '6', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (37, '6', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (38, '7', 'menu_id', NULL, 'bigint', 'Long', 'menuId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (39, '7', 'menu_name', NULL, 'character varying', 'String', 'menuName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (40, '7', 'parent_id', NULL, 'bigint', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 3, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (41, '7', 'order_num', NULL, 'integer', 'Long', 'orderNum', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 4, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (42, '7', 'path', NULL, 'character varying', 'String', 'path', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (43, '7', 'component', NULL, 'character varying', 'String', 'component', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (44, '7', 'query', NULL, 'character varying', 'String', 'query', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (45, '7', 'is_frame', NULL, 'character varying', 'String', 'isFrame', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (46, '7', 'is_cache', NULL, 'character varying', 'String', 'isCache', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (47, '7', 'menu_type', NULL, 'character varying', 'String', 'menuType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 10, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (48, '7', 'visible', NULL, 'character varying', 'String', 'visible', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (49, '7', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 12, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (50, '7', 'perms', NULL, 'character varying', 'String', 'perms', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (51, '7', 'icon', NULL, 'character varying', 'String', 'icon', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (52, '7', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 15, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (53, '7', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 16, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (54, '7', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 17, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (55, '7', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 18, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (56, '7', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 19, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (57, '8', 'config_id', NULL, 'integer', 'Long', 'configId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (58, '8', 'config_name', NULL, 'character varying', 'String', 'configName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (59, '8', 'config_key', NULL, 'character varying', 'String', 'configKey', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (60, '8', 'config_value', NULL, 'character varying', 'String', 'configValue', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (61, '8', 'config_type', NULL, 'character varying', 'String', 'configType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (62, '8', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (63, '8', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (64, '8', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (65, '8', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (66, '8', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (67, '9', 'dept_id', NULL, 'bigint', 'Long', 'deptId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (68, '9', 'parent_id', NULL, 'bigint', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (69, '9', 'ancestors', NULL, 'character varying', 'String', 'ancestors', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (70, '9', 'dept_name', NULL, 'character varying', 'String', 'deptName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (71, '9', 'order_num', NULL, 'integer', 'Long', 'orderNum', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (72, '9', 'leader', NULL, 'character varying', 'String', 'leader', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (73, '9', 'phone', NULL, 'character varying', 'String', 'phone', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (74, '9', 'email', NULL, 'character varying', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (75, '9', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (76, '9', 'del_flag', NULL, 'character varying', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (77, '9', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (78, '9', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (79, '9', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (80, '9', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (81, '10', 'dict_code', NULL, 'bigint', 'Long', 'dictCode', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (82, '10', 'dict_sort', NULL, 'integer', 'Long', 'dictSort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (83, '10', 'dict_label', NULL, 'character varying', 'String', 'dictLabel', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (84, '10', 'dict_value', NULL, 'character varying', 'String', 'dictValue', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (85, '10', 'dict_type', NULL, 'character varying', 'String', 'dictType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (86, '10', 'css_class', NULL, 'character varying', 'String', 'cssClass', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (87, '10', 'list_class', NULL, 'character varying', 'String', 'listClass', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (88, '10', 'is_default', NULL, 'character varying', 'String', 'isDefault', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (89, '10', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (90, '10', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (91, '10', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (92, '10', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (93, '10', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (94, '10', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (95, '11', 'dict_id', NULL, 'bigint', 'Long', 'dictId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (96, '11', 'dict_name', NULL, 'character varying', 'String', 'dictName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (97, '11', 'dict_type', NULL, 'character varying', 'String', 'dictType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (98, '11', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (99, '11', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (100, '11', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (101, '11', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (102, '11', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (103, '11', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (104, '12', 'job_id', NULL, 'bigint', 'Long', 'jobId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (105, '12', 'job_name', NULL, 'character varying', 'String', 'jobName', '1', '0', '0', '1', NULL, NULL, NULL, 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (106, '12', 'job_group', NULL, 'character varying', 'String', 'jobGroup', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (107, '12', 'invoke_target', NULL, 'character varying', 'String', 'invokeTarget', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (108, '12', 'cron_expression', NULL, 'character varying', 'String', 'cronExpression', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (109, '12', 'misfire_policy', NULL, 'character varying', 'String', 'misfirePolicy', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (110, '12', 'concurrent', NULL, 'character varying', 'String', 'concurrent', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (111, '12', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (112, '12', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (113, '12', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (114, '12', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (115, '12', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (116, '12', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (117, '13', 'job_log_id', NULL, 'bigint', 'Long', 'jobLogId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (118, '13', 'job_name', NULL, 'character varying', 'String', 'jobName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (119, '13', 'job_group', NULL, 'character varying', 'String', 'jobGroup', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (120, '13', 'invoke_target', NULL, 'character varying', 'String', 'invokeTarget', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (121, '13', 'job_message', NULL, 'character varying', 'String', 'jobMessage', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (122, '13', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (123, '13', 'exception_info', NULL, 'character varying', 'String', 'exceptionInfo', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (124, '13', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (125, '14', 'info_id', NULL, 'bigint', 'Long', 'infoId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (126, '14', 'user_name', NULL, 'character varying', 'String', 'userName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (127, '14', 'ipaddr', NULL, 'character varying', 'String', 'ipaddr', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (128, '14', 'login_location', NULL, 'character varying', 'String', 'loginLocation', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (129, '14', 'browser', NULL, 'character varying', 'String', 'browser', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (130, '14', 'os', NULL, 'character varying', 'String', 'os', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (131, '14', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (132, '14', 'msg', NULL, 'character varying', 'String', 'msg', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (133, '14', 'login_time', NULL, 'timestamp without time zone', 'String', 'loginTime', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (134, '15', 'oper_id', NULL, 'bigint', 'Long', 'operId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (135, '15', 'title', NULL, 'character varying', 'String', 'title', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (136, '15', 'business_type', NULL, 'integer', 'Long', 'businessType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (137, '15', 'method', NULL, 'character varying', 'String', 'method', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (138, '15', 'request_method', NULL, 'character varying', 'String', 'requestMethod', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (139, '15', 'operator_type', NULL, 'integer', 'Long', 'operatorType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (140, '15', 'oper_name', NULL, 'character varying', 'String', 'operName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (141, '15', 'dept_name', NULL, 'character varying', 'String', 'deptName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (142, '15', 'oper_url', NULL, 'character varying', 'String', 'operUrl', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (143, '15', 'oper_ip', NULL, 'character varying', 'String', 'operIp', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (144, '15', 'oper_location', NULL, 'character varying', 'String', 'operLocation', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (145, '15', 'oper_param', NULL, 'character varying', 'String', 'operParam', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (146, '15', 'json_result', NULL, 'character varying', 'String', 'jsonResult', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (147, '15', 'status', NULL, 'integer', 'Long', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (148, '15', 'error_msg', NULL, 'character varying', 'String', 'errorMsg', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 15, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (149, '15', 'oper_time', NULL, 'timestamp without time zone', 'String', 'operTime', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 16, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (150, '16', 'post_id', NULL, 'bigint', 'Long', 'postId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (151, '16', 'post_code', NULL, 'character varying', 'String', 'postCode', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (152, '16', 'post_name', NULL, 'character varying', 'String', 'postName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (153, '16', 'post_sort', NULL, 'integer', 'Long', 'postSort', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (154, '16', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (155, '16', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (156, '16', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (157, '16', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (158, '16', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (159, '16', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (160, '17', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (161, '17', 'role_name', NULL, 'character varying', 'String', 'roleName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (162, '17', 'role_key', NULL, 'character varying', 'String', 'roleKey', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (163, '17', 'role_sort', NULL, 'integer', 'Long', 'roleSort', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (164, '17', 'data_scope', NULL, 'character varying', 'String', 'dataScope', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (165, '17', 'menu_check_strictly', NULL, 'smallint', 'Long', 'menuCheckStrictly', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (166, '17', 'dept_check_strictly', NULL, 'smallint', 'Long', 'deptCheckStrictly', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (167, '17', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (168, '17', 'del_flag', NULL, 'character varying', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (169, '17', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (170, '17', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (171, '17', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (172, '17', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (173, '17', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (1, '1', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, '2024-01-16 11:33:08');
INSERT INTO basicdb_flowable.gen_table_column VALUES (2, '1', 'dept_id', NULL, 'bigint', 'Long', 'deptId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, '2024-01-16 11:33:08');
INSERT INTO basicdb_flowable.gen_table_column VALUES (174, '18', 'id', NULL, 'bigint', 'Long', 'id', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (175, '18', 'name', NULL, 'character varying', 'String', 'name', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (176, '18', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 3, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (177, '18', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 4, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (178, '18', 'create_id', NULL, 'bigint', 'Long', 'createId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 5, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (179, '18', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (180, '18', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (181, '18', 'update_id', NULL, 'bigint', 'Long', 'updateId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 8, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (182, '18', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 9, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (183, '18', 'is_del', NULL, 'character varying', 'String', 'isDel', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 10, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (184, '18', 'sex', NULL, 'character varying', 'String', 'sex', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 11, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable.gen_table_column VALUES (185, '18', 'birth', NULL, 'timestamp without time zone', 'String', 'birth', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 12, 'admin', '2024-02-01 11:09:28', NULL, NULL);


--
-- TOC entry 4397 (class 0 OID 4483654)
-- Dependencies: 274
-- Data for Name: qrtz_blob_triggers; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4398 (class 0 OID 4483659)
-- Dependencies: 275
-- Data for Name: qrtz_calendars; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4399 (class 0 OID 4483664)
-- Dependencies: 276
-- Data for Name: qrtz_cron_triggers; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4400 (class 0 OID 4483669)
-- Dependencies: 277
-- Data for Name: qrtz_fired_triggers; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4401 (class 0 OID 4483674)
-- Dependencies: 278
-- Data for Name: qrtz_job_details; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4402 (class 0 OID 4483679)
-- Dependencies: 279
-- Data for Name: qrtz_locks; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4403 (class 0 OID 4483682)
-- Dependencies: 280
-- Data for Name: qrtz_paused_trigger_grps; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4404 (class 0 OID 4483685)
-- Dependencies: 281
-- Data for Name: qrtz_scheduler_state; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4405 (class 0 OID 4483688)
-- Dependencies: 282
-- Data for Name: qrtz_simple_triggers; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4406 (class 0 OID 4483693)
-- Dependencies: 283
-- Data for Name: qrtz_simprop_triggers; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4407 (class 0 OID 4483698)
-- Dependencies: 284
-- Data for Name: qrtz_triggers; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4408 (class 0 OID 4483703)
-- Dependencies: 285
-- Data for Name: sys_config; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_config VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO basicdb_flowable.sys_config VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO basicdb_flowable.sys_config VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO basicdb_flowable.sys_config VALUES (10, '账号自助-密码有效期天数', 'sys.account.checkPassword.time', '90', 'Y', 'admin', '2024-02-21 11:21:18', 'admin', '2024-10-11 10:14:33', '对90天未修改密码的账号进行提醒');
INSERT INTO basicdb_flowable.sys_config VALUES (4, '账号自助-验证码开关', 'sys.account.captchaOnOff', 'true', 'Y', 'admin', '2023-02-28 17:09:30', 'admin', '2024-01-31 17:09:54', '是否开启验证码功能（true开启，false关闭）');
INSERT INTO basicdb_flowable.sys_config VALUES (7, '密码复杂度验证表达式', 'sys.password.inputPattern', '/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[`!@#$%^&*(\)-//.,;'':"{}\[\]\\|]).{8,15}$/', 'Y', 'admin', '2024-02-20 15:00:42', 'admin', '2024-02-20 15:55:26', '/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[`!@#$%^&*(\)-//.,;'':"{}\[\]\\|]).{8,15}$/');
INSERT INTO basicdb_flowable.sys_config VALUES (8, '密码复杂度验证提示信息', 'sys.password.errorMessage', '用户密码长度必须介于 8 和 15 之间且包含大小写字母、数字及特殊字符', 'Y', 'admin', '2024-02-20 15:11:04', 'admin', '2024-02-20 16:03:43', NULL);
INSERT INTO basicdb_flowable.sys_config VALUES (9, '账号自助-密码修改提醒', 'sys.account.checkPassword', 'true', 'Y', 'admin', '2024-02-21 10:59:19', NULL, NULL, '是否开启修改密码提醒(对长时间没有修改密码的账号)（true开启，false关闭）');
INSERT INTO basicdb_flowable.sys_config VALUES (2, '账号管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '初始化密码 123456');
INSERT INTO basicdb_flowable.sys_config VALUES (11, '账号登录-是否开启账号锁定', 'user.password.lockOpen', 'true', 'Y', 'admin', '2024-02-21 11:23:26', NULL, NULL, '是否开启密码错误次数过多账号锁定（true开启，false关闭）');
INSERT INTO basicdb_flowable.sys_config VALUES (12, '账号登录-密码错误次数锁定', 'user.password.maxRetryCount ', '3', 'Y', 'admin', '2024-02-21 11:25:02', NULL, NULL, '账号登录时密码最大错误次数');
INSERT INTO basicdb_flowable.sys_config VALUES (13, '账号登录-账号锁定时间', 'user.password.lockTime', '30', 'Y', 'admin', '2024-02-21 11:26:27', NULL, NULL, '账号锁定时长（分钟）');


--
-- TOC entry 4410 (class 0 OID 4483710)
-- Dependencies: 287
-- Data for Name: sys_demo; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--

INSERT INTO basicdb_flowable.sys_demo VALUES (1750711753414205442, '李四', 'admin', '2024-01-26 10:46:23', 1, 'admin', '2024-02-19 11:34:34.706', NULL, NULL, '0', 'boy', '2024-01-09 00:00:00');


--
-- TOC entry 4411 (class 0 OID 4483713)
-- Dependencies: 288
-- Data for Name: sys_dept; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_dept VALUES (103, 101, '0,100,101', '研发部门', 1, '部门管理员', '15888888888', 'bdvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-07-10 17:08:12', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_dept VALUES (101, 100, '0,100', '佛山总公司', 1, '佛山管理员', '18888888888', 'bdvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-01-26 14:39:26', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_dept VALUES (100, 0, '0', '集团公司', 0, '管理员', '18888888888', 'dbvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-02-08 08:49:44', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_dept VALUES (102, 100, '0,100', '长沙分公司', 2, '长沙管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_dept VALUES (104, 101, '0,100,101', '市场部门', 2, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_dept VALUES (105, 101, '0,100,101', '测试部门', 3, '部门管理员', '15888888888', 'bdvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-02-08 08:49:31', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_dept VALUES (106, 101, '0,100,101', '财务部门', 4, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_dept VALUES (107, 101, '0,100,101', '运维部门', 5, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_dept VALUES (108, 102, '0,100,102', '市场部门', 1, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_dept VALUES (109, 102, '0,100,102', '财务部门', 2, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_dept VALUES (111, 101, '0,100,101', '办公室', 3, NULL, NULL, NULL, '0', '0', 'xiaoxi', '2024-04-22 16:04:38', NULL, NULL, NULL, NULL, NULL);


--
-- TOC entry 4413 (class 0 OID 4483723)
-- Dependencies: 290
-- Data for Name: sys_dict_data; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_dict_data VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '性别男');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '性别女');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '性别未知');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '显示菜单');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '隐藏菜单');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '停用状态');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '停用状态');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '默认分组');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统分组');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统默认是');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统默认否');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '通知');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '公告');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '关闭状态');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (18, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '新增操作');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (19, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '修改操作');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (20, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '删除操作');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (21, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '授权操作');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (22, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '导出操作');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (23, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '导入操作');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (24, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '强退操作');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (25, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '生成操作');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (26, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '清空操作');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (27, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (28, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '停用状态');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (30, 0, '跟随系统', '0', 'sys_show_tags', NULL, 'default', 'N', '0', 'admin', '2024-06-25 11:15:44', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_dict_data VALUES (31, 1, '显示', '1', 'sys_show_tags', NULL, 'default', 'N', '0', 'admin', '2024-06-25 11:16:01', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_dict_data VALUES (32, 2, '隐藏', '2', 'sys_show_tags', NULL, 'default', 'N', '0', 'admin', '2024-06-25 11:17:07', 'admin', '2024-06-25 11:17:13', NULL);
INSERT INTO basicdb_flowable.sys_dict_data VALUES (33, 1, '草稿', 'draft', 'wf_process_status', NULL, 'info', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '流程草稿');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (34, 2, '审批中', 'running', 'wf_process_status', NULL, 'primary', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '审批中');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (35, 3, '已结束', 'finished', 'wf_process_status', NULL, 'success', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '已完成');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (36, 4, '已撤回', 'revoke', 'wf_process_status', NULL, 'warning', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '已撤回');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (37, 5, '已取消', 'cancel', 'wf_process_status', NULL, 'danger', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '已取消');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (38, 1, '通过', '1', 'wf_process_action', NULL, 'success', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '审批通过');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (39, 2, '驳回', '2', 'wf_process_action', NULL, 'danger', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '驳回');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (40, 3, '退回', '3', 'wf_process_action', NULL, 'warning', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '退回上一步');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (41, 4, '转办', '4', 'wf_process_action', NULL, 'primary', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '转办他人');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (42, 5, '委派', '5', 'wf_process_action', NULL, 'primary', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '委派');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (43, 6, '加签', '6', 'wf_process_action', NULL, 'primary', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '加签');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (44, 7, '撤回', '7', 'wf_process_action', NULL, 'warning', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '发起人撤回');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (45, 8, '取消', '8', 'wf_process_action', NULL, 'danger', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '发起人取消');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (46, 1, '自定义表单', 'custom', 'wf_process_form_type', NULL, 'primary', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, 'VFormDesigner 自定义');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (47, 2, '系统表单', 'system', 'wf_process_form_type', NULL, 'info', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '系统内置表单');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (48, 3, '外部表单', 'external', 'wf_process_form_type', NULL, 'success', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '外部业务表单');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (49, 1, '启用', '0', 'wf_expression_status', NULL, 'primary', 'Y', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '启用');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (50, 2, '停用', '1', 'wf_expression_status', NULL, 'danger', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '停用');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (51, 1, '条件表达式', 'condition', 'wf_expression_type', NULL, 'primary', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '网关条件');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (52, 2, '人员表达式', 'assignee', 'wf_expression_type', NULL, 'success', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '节点处理人');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (53, 3, '变量表达式', 'variable', 'wf_expression_type', NULL, 'info', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '流程变量');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (54, 1, '未读', '0', 'wf_copy_status', NULL, 'warning', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '未读');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (55, 2, '已读', '1', 'wf_copy_status', NULL, 'success', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '已读');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (56, 1, '激活', 'active', 'wf_deploy_status', NULL, 'success', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '已激活');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (57, 2, '挂起', 'suspended', 'wf_deploy_status', NULL, 'warning', 'N', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '已挂起');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (58, 1, '同意', 'complete', 'wf_process_permission', NULL, 'success', 'N', '0', 'admin', '2026-10-15 11:56:58', NULL, NULL, '同意/通过');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (59, 2, '驳回', 'reject', 'wf_process_permission', NULL, 'danger', 'N', '0', 'admin', '2026-10-15 11:56:58', NULL, NULL, '驳回');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (60, 3, '退回', 'return', 'wf_process_permission', NULL, 'warning', 'N', '0', 'admin', '2026-10-15 11:56:58', NULL, NULL, '退回到指定节点');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (61, 4, '转办', 'transfer', 'wf_process_permission', NULL, 'primary', 'N', '0', 'admin', '2026-10-15 11:56:58', NULL, NULL, '转办给他人处理');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (62, 5, '委派', 'delegate', 'wf_process_permission', NULL, 'info', 'N', '0', 'admin', '2026-10-15 11:56:58', NULL, NULL, '委派他人处理');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (63, 6, '抄送', 'copy', 'wf_process_permission', NULL, NULL, 'N', '0', 'admin', '2026-10-15 11:56:58', NULL, NULL, '抄送给他人');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (64, 7, '会签回退', 'mulitiSequentialUnComplete', 'wf_process_permission', NULL, 'warning', 'N', '0', 'admin', '2026-10-15 14:08:13', NULL, NULL, '顺序会签节点回退给上一位处理人（仅顺序会签节点生效）');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (65, 8, '前加签', 'addSignBefore', 'wf_process_permission', NULL, 'primary', 'N', '0', 'admin', '2026-10-15 16:40:40', NULL, NULL, '在当前节点前插入审批人：被加签人先办，办完回到自己');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (66, 9, '后加签', 'addSignAfter', 'wf_process_permission', NULL, 'primary', 'N', '0', 'admin', '2026-10-15 16:40:40', NULL, NULL, '在当前节点后插入审批人：自己先办完，再让被加签人补审');
INSERT INTO basicdb_flowable.sys_dict_data VALUES (67, 10, '终止', 'stop', 'wf_process_permission', NULL, 'danger', 'N', '0', 'admin', '2026-10-15 16:40:40', NULL, NULL, '结束整个流程（历史/审批意见/表单数据保留）');


--
-- TOC entry 4415 (class 0 OID 4483732)
-- Dependencies: 292
-- Data for Name: sys_dict_type; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_dict_type VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '用户性别列表');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '菜单状态列表');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统开关列表');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '任务状态列表');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '任务分组列表');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统是否列表');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '通知类型列表');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '通知状态列表');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '操作类型列表');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '登录状态列表');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (12, '选项卡状态', 'sys_show_tags', '0', 'admin', '2024-06-25 11:14:52', 'admin', '2024-06-25 11:14:59', '自定义展示系统Tags-View组件');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (13, '流程状态', 'wf_process_status', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '流程实例状态');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (14, '流程动作', 'wf_process_action', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '审批动作类型');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (15, '流程表单类型', 'wf_process_form_type', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '表单类型');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (16, '流程表达式状态', 'wf_expression_status', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '流程表达式启用状态');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (17, '流程表达式类型', 'wf_expression_type', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '流程表达式分类');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (18, '抄送状态', 'wf_copy_status', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '抄送处理状态');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (19, '流程部署状态', 'wf_deploy_status', '0', 'admin', '2026-10-14 15:56:32', NULL, NULL, '流程部署状态');
INSERT INTO basicdb_flowable.sys_dict_type VALUES (20, '流程节点授权功能', 'wf_process_permission', '0', 'admin', '2026-10-15 11:56:58', NULL, NULL, '用户任务节点允许的审批操作按钮');


--
-- TOC entry 4417 (class 0 OID 4483739)
-- Dependencies: 294
-- Data for Name: sys_files; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4418 (class 0 OID 4483744)
-- Dependencies: 295
-- Data for Name: sys_i18n; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--

INSERT INTO basicdb_flowable.sys_i18n VALUES ('Assembly.buttonPosition', 'Please configure the buttonPosition property for the chart', 'en');
INSERT INTO basicdb_flowable.sys_i18n VALUES ('Assembly.buttonPosition', '请给图表配置buttonPosition属性', 'zh_CN');
INSERT INTO basicdb_flowable.sys_i18n VALUES ('Assembly.buttonPosition', '請給圖表配置buttonPosition屬性', 'zh_hant');
INSERT INTO basicdb_flowable.sys_i18n VALUES ('Assembly.idFormStyle', 'Please configure the idFormStyle property for the chart for more information', 'en');
INSERT INTO basicdb_flowable.sys_i18n VALUES ('Assembly.idFormStyle', '请给图表配置idFormStyle属性', 'zh_CN');
INSERT INTO basicdb_flowable.sys_i18n VALUES ('Assembly.idFormStyle', '請給圖表配置idFormStyle屬性', 'zh_hant');


--
-- TOC entry 4419 (class 0 OID 4483749)
-- Dependencies: 296
-- Data for Name: sys_job; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_job VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2023-02-28 17:09:30', '', NULL, '');
INSERT INTO basicdb_flowable.sys_job VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(''ry'')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2023-02-28 17:09:30', '', NULL, '');
INSERT INTO basicdb_flowable.sys_job VALUES (3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(''ry'', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2023-02-28 17:09:30', '', NULL, '');


--
-- TOC entry 4421 (class 0 OID 4483759)
-- Dependencies: 298
-- Data for Name: sys_job_log; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4423 (class 0 OID 4483766)
-- Dependencies: 300
-- Data for Name: sys_logininfor; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_logininfor VALUES (725, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-14 16:14:28');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (726, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '1', '验证码已失效', '2026-10-14 21:41:42');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (727, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-14 21:41:44');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (728, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '退出成功', '2026-10-14 23:39:39');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (729, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-14 23:39:42');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (730, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-15 08:39:48');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (731, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-15 11:27:08');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (732, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-15 13:34:49');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (733, 'xiaoxi', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-15 14:21:54');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (734, 'xiaoxi', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '退出成功', '2026-10-15 14:25:07');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (735, 'xiaoxi', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-15 14:25:12');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (736, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-15 16:28:18');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (737, 'xiaoxi', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-15 16:43:12');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (738, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-15 23:24:20');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (739, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-16 08:53:57');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (740, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '退出成功', '2026-10-16 08:55:26');
INSERT INTO basicdb_flowable.sys_logininfor VALUES (741, 'admin', '127.0.0.1', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-10-16 08:55:28');


--
-- TOC entry 4425 (class 0 OID 4483773)
-- Dependencies: 302
-- Data for Name: sys_menu; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_menu VALUES (3, '系统工具', 0, 3, 'tool', NULL, '', '1', '0', 'M', '0', '0', '', 'tool', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-13 16:16:59', '系统工具目录', NULL, '0', 'system tools', '系統工具');
INSERT INTO basicdb_flowable.sys_menu VALUES (1, '系统管理', 0, 1, 'system', NULL, '', '1', '0', 'M', '0', '0', '', 'system', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-13 16:17:15', '系统管理目录', NULL, '0', 'system management', '系統管理');
INSERT INTO basicdb_flowable.sys_menu VALUES (1013, '菜单查询', 102, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1014, '菜单新增', 102, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1015, '菜单修改', 102, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1016, '菜单删除', 102, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1024, '岗位删除', 104, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:post:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1025, '岗位导出', 104, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:post:export', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (108, '日志管理', 1, 9, 'log', '', '', '1', '0', 'M', '0', '0', '', 'log', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:02', '日志管理菜单', NULL, '0', 'log management', '日誌管理');
INSERT INTO basicdb_flowable.sys_menu VALUES (1045, '日志导出', 501, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1001, '用户查询', 100, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:user:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', 'user query', '用戶查詢');
INSERT INTO basicdb_flowable.sys_menu VALUES (1002, '用户新增', 100, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:user:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1003, '用户修改', 100, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:user:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1008, '角色查询', 101, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:role:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1009, '角色新增', 101, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:role:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1010, '角色修改', 101, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:role:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1011, '角色删除', 101, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:role:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:55', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1019, '部门修改', 103, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1020, '部门删除', 103, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1030, '字典导出', 105, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1058, '导入代码', 115, 2, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1057, '生成删除', 115, 3, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1059, '预览代码', 115, 4, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1060, '生成代码', 115, 5, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1047, '批量强退', 109, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:05', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1004, '用户删除', 100, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:user:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1005, '用户导出', 100, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:user:export', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1006, '用户导入', 100, 6, '', '', '', '1', '0', 'F', '0', '0', 'system:user:import', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1007, '重置密码', 100, 7, '', '', '', '1', '0', 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1012, '角色导出', 101, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:role:export', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:55', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1017, '部门查询', 103, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:14', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1018, '部门新增', 103, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:14', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1021, '岗位查询', 104, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:post:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1022, '岗位新增', 104, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:post:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1023, '岗位修改', 104, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:post:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1026, '字典查询', 105, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1027, '字典新增', 105, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1028, '字典修改', 105, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1029, '字典删除', 105, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1031, '参数查询', 106, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1032, '参数新增', 106, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:add', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1033, '参数修改', 106, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '1', '0', 'C', '0', '0', 'system:user:list', 'user', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '用户管理菜单', NULL, '0', 'user management', '用戶管理');
INSERT INTO basicdb_flowable.sys_menu VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '1', '0', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '角色管理菜单', NULL, '0', 'role management', '角色管理');
INSERT INTO basicdb_flowable.sys_menu VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', '1', '0', 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:14', '部门管理菜单', NULL, '0', 'sector management', '部門管理');
INSERT INTO basicdb_flowable.sys_menu VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', '1', '0', 'C', '0', '0', 'system:post:list', 'post', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:35', '岗位管理菜单', NULL, '0', 'post management', '崗位管理');
INSERT INTO basicdb_flowable.sys_menu VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', '1', '0', 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:42', '字典管理菜单', NULL, '0', 'dictionary management', '字典管理');
INSERT INTO basicdb_flowable.sys_menu VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', '1', '0', 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:51', '参数设置菜单', NULL, '0', 'parameter setting', '參數設置');
INSERT INTO basicdb_flowable.sys_menu VALUES (1955896250820567041, '国际化', 1, 10, 'i18n', NULL, NULL, '1', '0', 'C', '0', '0', '', 'language', 'admin', '2025-08-14 15:36:20', 'admin', '2025-08-14 15:36:59', NULL, NULL, NULL, 'internationalization', '國際化');
INSERT INTO basicdb_flowable.sys_menu VALUES (1034, '参数删除', 106, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1036, '公告查询', 107, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1037, '公告新增', 107, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:add', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1038, '公告修改', 107, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1039, '公告删除', 107, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1040, '操作查询', 500, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:09', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1041, '操作删除', 500, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:09', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1042, '日志导出', 500, 4, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:09', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1043, '登录查询', 501, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1044, '登录删除', 501, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', '1', '0', 'M', '1', '1', '', 'monitor', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:21', '系统监控目录', NULL, '0', 'system monitoring', '系統監控');
INSERT INTO basicdb_flowable.sys_menu VALUES (1055, '生成查询', 115, 1, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1056, '生成修改', 115, 2, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1046, '在线查询', 109, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:05', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1049, '任务查询', 110, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:12', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1050, '任务新增', 110, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:12', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1753952621629796354, '查询', 1061, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'business:demo:query', '#', 'admin', '2024-02-04 09:24:27', 'admin', '2024-02-06 16:08:37', NULL, NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1753952621629796355, '新增', 1061, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'business:demo:add', '#', 'admin', '2024-02-04 09:24:27', 'admin', '2024-02-06 16:08:37', NULL, NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1753952621629796356, '修改', 1061, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'business:demo:edit', '#', 'admin', '2024-02-04 09:24:27', 'admin', '2024-02-06 16:08:37', NULL, NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1753952621629796357, '删除', 1061, 5, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'business:demo:remove', '#', 'admin', '2024-02-04 09:24:27', 'admin', '2024-02-06 16:08:37', NULL, NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1753952621629796358, '导出', 1061, 6, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'business:demo:export', '#', 'admin', '2024-02-04 09:24:27', 'admin', '2024-02-06 16:08:37', NULL, NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1754778882690195458, '页面示例', 3, 0, 'business', NULL, NULL, '1', '0', 'M', '0', '0', '', 'tab', 'admin', '2024-02-06 16:07:43', 'admin', '2024-05-28 17:52:59', NULL, NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1035, '参数导出', 106, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1048, '单条强退', 109, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:05', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1051, '任务修改', 110, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1052, '任务删除', 110, 4, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1053, '状态修改', 110, 5, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1054, '任务导出', 110, 7, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '1', '0', 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:36', '菜单管理菜单', NULL, '0', 'menu management', '菜單管理');
INSERT INTO basicdb_flowable.sys_menu VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', '1', '0', 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:57', '通知公告菜单', NULL, '0', 'notice announcement', '通知公告');
INSERT INTO basicdb_flowable.sys_menu VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', '1', '0', 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:09', '操作日志菜单', NULL, '0', 'operation log', '操作日誌');
INSERT INTO basicdb_flowable.sys_menu VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '1', '0', 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:15', '登录日志菜单', NULL, '0', 'login logs', '登錄日誌');
INSERT INTO basicdb_flowable.sys_menu VALUES (111, '数据监控', 3, 4, 'druid', 'monitor/druid/index', '', '1', '0', 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:59', '数据监控菜单', NULL, '0', 'data monitoring', '數據監控');
INSERT INTO basicdb_flowable.sys_menu VALUES (109, '在线用户', 3, 5, 'online', 'monitor/online/index', '', '1', '0', 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:05', '在线用户菜单', NULL, '0', 'online users', '在線用戶');
INSERT INTO basicdb_flowable.sys_menu VALUES (112, '服务监控', 3, 7, 'server', 'monitor/server/index', '', '1', '0', 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:21', '服务监控菜单', NULL, '0', 'service monitoring', '服務監控');
INSERT INTO basicdb_flowable.sys_menu VALUES (113, '缓存监控', 3, 8, 'cache', 'monitor/cache/index', '', '1', '0', 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:27', '缓存监控菜单', NULL, '0', 'Cache monitoring', '緩存監控');
INSERT INTO basicdb_flowable.sys_menu VALUES (114, '表单构建', 3, 1, 'build', 'tool/build/index', '', '1', '0', 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:36', '表单构建菜单', NULL, '0', 'Form construction', '表單構建');
INSERT INTO basicdb_flowable.sys_menu VALUES (115, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', '1', '0', 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:44', '代码生成菜单', NULL, '0', 'code generation', '代碼生成');
INSERT INTO basicdb_flowable.sys_menu VALUES (116, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', '1', '0', 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:54', '系统接口菜单', NULL, '0', 'system interface', '系統接口');
INSERT INTO basicdb_flowable.sys_menu VALUES (110, '定时任务', 3, 6, 'job', 'monitor/job/index', '', '1', '0', 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:11', '定时任务菜单', NULL, '0', 'scheduled task', '定時任務');
INSERT INTO basicdb_flowable.sys_menu VALUES (1061, '表单示例', 1754778882690195458, 10, 'demo', NULL, NULL, '1', '1', 'C', '0', '0', 'business:demo:list', 'education', 'admin', '2024-01-26 09:46:30', 'admin', '2024-02-07 12:00:10', NULL, NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1753950971045343233, '示例表单', 1061, 0, 'demoForm', NULL, NULL, '1', '1', 'C', '1', '0', 'business:demo:add,business:demo:edit,business:demo:query', '', 'admin', '2024-02-04 09:17:53', 'admin', '2024-02-06 16:08:37', NULL, '0', '0', NULL, NULL);
INSERT INTO basicdb_flowable.sys_menu VALUES (1561, '表单新增', 1509, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:saveForm', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1506, '流程分类', 1500, 6, 'category', 'workflow/category/index', NULL, '1', '0', 'C', '0', '0', 'workflow:category:list', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '流程分类管理', NULL, NULL, 'Category', '流程分類');
INSERT INTO basicdb_flowable.sys_menu VALUES (1507, '流程模型', 1500, 7, 'model', 'workflow/model/index', NULL, '1', '0', 'C', '0', '0', 'workflow:model:list', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '流程模型管理', NULL, NULL, 'Model', '流程模型');
INSERT INTO basicdb_flowable.sys_menu VALUES (1509, '表单管理', 1500, 9, 'form', 'workflow/form/index', NULL, '1', '0', 'C', '0', '0', 'workflow:form:list', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '流程表单管理', NULL, NULL, 'Form', '表單管理');
INSERT INTO basicdb_flowable.sys_menu VALUES (1510, '表达式管理', 1500, 10, 'expression', 'workflow/expression/index', NULL, '1', '0', 'C', '0', '0', 'workflow:expression:list', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '表达式字典管理', NULL, NULL, 'Expression', '表達式管理');
INSERT INTO basicdb_flowable.sys_menu VALUES (1530, '分类查询', 1506, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:query', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1531, '分类新增', 1506, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:add', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1532, '分类修改', 1506, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:edit', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1533, '分类删除', 1506, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:remove', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1540, '模型查询', 1507, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:list', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1541, '模型新增', 1507, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:add', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1542, '模型修改', 1507, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:edit', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1543, '模型删除', 1507, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:remove', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1544, '模型部署', 1507, 5, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:deploy', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1501, '我的待办', 1500, 1, 'task/todo', 'workflow/task/todo', NULL, '1', '0', 'C', '0', '0', 'workflow:process:todoList', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '我的待办任务', NULL, NULL, 'Todo', '我的待辦');
INSERT INTO basicdb_flowable.sys_menu VALUES (1502, '我的已办', 1500, 2, 'task/finished', 'workflow/task/finished', NULL, '1', '0', 'C', '0', '0', 'workflow:process:finishedList', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '我的已办任务', NULL, NULL, 'Finished', '我的已辦');
INSERT INTO basicdb_flowable.sys_menu VALUES (1503, '我的已发', 1500, 3, 'task/own', 'workflow/task/own', NULL, '1', '0', 'C', '0', '0', 'workflow:process:ownList', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '我发起的流程', NULL, NULL, 'Own', '我的已發');
INSERT INTO basicdb_flowable.sys_menu VALUES (1504, '抄送我的', 1500, 4, 'task/copy', 'workflow/task/copy', NULL, '1', '0', 'C', '0', '0', 'workflow:process:copyList', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '抄送我的任务', NULL, NULL, 'Copy', '抄送我的');
INSERT INTO basicdb_flowable.sys_menu VALUES (1505, '待签收', 1500, 5, 'task/claim', 'workflow/task/claim', NULL, '1', '0', 'C', '0', '0', 'workflow:process:claimList', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '待签收任务', NULL, NULL, 'Claim', '待簽收');
INSERT INTO basicdb_flowable.sys_menu VALUES (1560, '表单查询', 1509, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:getInfo', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1500, '流程引擎', 0, 5, 'workflow', NULL, NULL, '1', '0', 'M', '0', '0', '', 'tree', 'admin', '2026-10-14 16:31:42', 'admin', '2026-10-15 08:40:09', '工作流目录', NULL, NULL, 'Workflow', '工作流程');
INSERT INTO basicdb_flowable.sys_menu VALUES (1580, '表达式查询', 1510, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:list', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1581, '表达式新增', 1510, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:add', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1582, '表达式修改', 1510, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:edit', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1583, '表达式删除', 1510, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:remove', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1570, '任务审批', 1501, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:complete', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1571, '任务拒绝', 1501, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:reject', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1572, '任务转办', 1501, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:transfer', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1573, '任务委派', 1501, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:delegate', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1574, '任务退回', 1501, 5, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:return', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1575, '任务加签', 1501, 6, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:beforeAddSignTask', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1576, '任务终止', 1501, 7, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:cancel', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1577, '流程撤回', 1503, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:revokeProcess', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1562, '表单修改', 1509, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:saveForm', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1563, '表单删除', 1509, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:delForm', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1601, '分类导出', 1506, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:export', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1602, '分类下拉选项', 1506, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:getCategoryOption', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1603, '分类列表(不分页)', 1506, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:listWithoutPage', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1606, '表达式导出', 1510, 6, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:export', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1607, '表达式详情', 1510, 7, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:getInfo', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1608, '按编码查表单', 1509, 8, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:getFormByCode', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1609, '表单控件列表', 1509, 9, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:getVFormWidgetList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1610, '表单模板全量', 1509, 10, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:allList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1611, '表单模板删除', 1509, 11, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:delFormTemplate', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1612, '表单模板详情', 1509, 12, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:getInfo', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1613, '表单模板查询', 1509, 13, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:list', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1614, '表单模板保存', 1509, 14, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:saveForm', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1615, '模型激活', 1507, 15, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:activate', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1616, '模型XML', 1507, 16, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:bpmnXml', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1617, '模型导出', 1507, 17, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:export', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1618, '分类下模型', 1507, 18, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:getCategoryToModel', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1619, '模型详情', 1507, 19, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:getInfo', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1620, '历史模型', 1507, 20, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:historyList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1621, '设为最新', 1507, 21, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:latest', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1622, '可发起模型', 1507, 22, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:listModelLaunch', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1623, '模型发布(发起)', 1507, 23, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:publishModel', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1624, '模型保存', 1507, 24, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:save', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1625, '模型挂起', 1507, 25, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:suspended', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1626, '流程作废', 1500, 26, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:abolishProcess', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1627, '后加签', 1500, 27, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:afterAddSignTask', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1628, '流程XML', 1500, 28, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:bpmnXml', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1629, '检查流程是否结束', 1500, 29, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:checkFlowIsEnd', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1630, '是否审批人', 1500, 30, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:checkIsAuditUser', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1631, '是否发起人', 1500, 31, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:checkIsStartUser', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1632, '流程是否存在', 1500, 32, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:checkProcessIsExistByProcInsId', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1633, '签收任务', 1500, 33, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:claim', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1634, '待签收导出', 1500, 34, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:claimExport', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1635, '按业务key完成', 1500, 35, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:completeByBusinessKey', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1636, '抄送导出', 1500, 36, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:copyExport', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1637, '删除流程实例', 1500, 37, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:deleteProcessByIds', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1638, '删除流程任务', 1500, 38, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:deleteProcessTask', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1639, '流程详情', 1500, 39, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:detail', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1640, '流程图', 1500, 40, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:diagram', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1641, '变更处理人', 1500, 41, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:editProcessTaskAssignee', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1642, '已办导出', 1500, 42, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:finishedExport', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1643, '抄送流程列表', 1500, 43, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getCopyProcessList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1644, '当前任务信息', 1500, 44, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getCurrentTaskInfo', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1645, '当前任务节点', 1500, 45, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getCurrentTaskNode', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1646, '流程附件', 1500, 46, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getFilesByProcessInstanceId', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1647, '流程流转图', 1500, 47, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getFlowViewer', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1648, '下一节点', 1500, 48, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getNextNode', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1649, '我发起的(不分页)', 1500, 49, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getOwnProcessList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1650, '实例扩展信息', 1500, 50, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcInsIdToExtends', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1651, '实例任务', 1500, 51, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcInsIdToTask', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1652, '审批意见', 1500, 52, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcessCommentInfo', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1653, '流程表单', 1500, 53, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcessForm', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1654, '按业务key查流程', 1500, 54, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcessInfoByBusinessKey', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1655, '流程模型key', 1500, 55, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcessModelKey', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1656, '流程变量', 1500, 56, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcessVariables', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1657, '待办与抄送', 1500, 57, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getTaskTodoAndCopy', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1658, '任务变量', 1500, 58, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getTaskVariables', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1659, '待发起模型', 1500, 59, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getTodoModel', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1660, '历史节点', 1500, 60, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:historyProcNodeList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1661, '会签回退', 1500, 61, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:mulitiSequentialUnComplete', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1662, '下一节点类型', 1500, 62, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:nextFlowNode', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1663, '我的已发导出', 1500, 63, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:ownExport', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1664, '流程表单列表', 1500, 64, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:processFormList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1665, '抄送已读', 1500, 65, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:readCopy', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1666, '驳回到上一节点', 1500, 66, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:rejectLastTask', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1667, '可回退节点', 1500, 67, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:returnList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1668, '保存附件到模型', 1500, 68, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:saveFileToModel', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1669, '已办(选择)', 1500, 69, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:selectFinishedProcessList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1670, '历史实例', 1500, 70, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:selectHisProcInsProcess', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1671, '所有任务', 1500, 71, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:selectProcessAllTask', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1672, '所有实例', 1500, 72, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:selectProcessPrcInstance', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1673, '用户任务', 1500, 73, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:selectProcessTaskByUser', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1674, '跳过节点', 1500, 74, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:skipUserTask', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1675, '待办导出', 1500, 75, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:todoExport', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1676, '取消签收', 1500, 76, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:unClaim', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1677, '更新任务评论', 1500, 77, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:updateTaskComment', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1678, '表单分类新增', 1509, 78, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:systemCategory:add', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1679, '表单分类修改', 1509, 79, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:systemCategory:edit', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1680, '表单分类查询', 1509, 80, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:systemCategory:list', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1681, '表单分类删除', 1509, 81, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:systemCategory:remove', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1682, '删除流程表单', 1500, 82, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:delProcessForm', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1683, '按业务key删表单', 1500, 83, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:delProcessFormByBusinessKey', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1684, '按实例与节点删表单', 1500, 84, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:delProcessFormByProcInsIdAndActId', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1685, '保存流程表单', 1500, 85, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:saveProcessForm', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1686, '按实例查表单数据', 1500, 86, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:selectFormDataByProcInsIdToList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1687, '按业务key查表单列表', 1500, 87, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:selectFormJsonDataByBusinessKeyToList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1688, '按实例与节点查表单', 1500, 88, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:selectFormJsonDataByProcInsIdAndActIdToList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1689, '按实例与业务key查表单', 1500, 89, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:selectFormJsonDataByProcInsIdAndBusinessKey', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1690, '按任务与节点查表单', 1500, 90, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:selectFormJsonDataByTaskIdAndActIdToList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1691, '按业务key改字段', 1500, 91, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:updateFieldValueByBusinessKey', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable.sys_menu VALUES (1511, '流程中心', 1500, 0, 'center', 'workflow/center/index', NULL, '1', '0', 'C', '0', '0', 'workflow:process:selectProcessPrcInstance', '#', 'admin', '2026-10-15 11:33:56', NULL, NULL, '系统上正在流转的流程，可直接废除', NULL, NULL, 'Process Center', '流程中心');
INSERT INTO basicdb_flowable.sys_menu VALUES (1699, '测试启动流程', 1500, 20, 'process/start', 'workflow/process/start', NULL, '1', '0', 'C', '0', '0', 'workflow:model:publishModel', '#', 'admin', '2026-10-15 10:15:13', NULL, NULL, '快速测试流程发起与流转（仅测试用途）', NULL, NULL, 'Test Start', '測試啟動流程');
INSERT INTO basicdb_flowable.sys_menu VALUES (1955896250820567043, '执行替换', 1955896250820567042, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:migrate', '#', 'admin', '2026-10-16 08:53:07', NULL, NULL, '对运行中流程执行替换', NULL, NULL, 'Replace', '執行替換');
INSERT INTO basicdb_flowable.sys_menu VALUES (1955896250820567044, '校验参数', 1955896250820567042, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:migrate', '#', 'admin', '2026-10-16 08:53:07', NULL, NULL, '校验替换参数（不实际执行）', NULL, NULL, 'Validate', '校驗參數');
INSERT INTO basicdb_flowable.sys_menu VALUES (1955896250820567045, '列新流程 userTask', 1955896250820567042, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:migrate', '#', 'admin', '2026-10-16 08:53:07', NULL, NULL, '取新流程定义的 userTask 节点列表（前端智能推荐用）', NULL, NULL, 'List UserTasks', '列新流程用戶任務');
INSERT INTO basicdb_flowable.sys_menu VALUES (1955896250820567046, '取旧流程当前 task', 1955896250820567042, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:migrate', '#', 'admin', '2026-10-16 08:53:07', NULL, NULL, '取旧流程当前 taskDefinitionKey（前端智能推荐用）', NULL, NULL, 'Get Current Task', '取舊流程當前任務');
INSERT INTO basicdb_flowable.sys_menu VALUES (1955896250820567042, '流程迁移', 1500, 31, 'migrate', 'workflow/migrate/index', NULL, '1', '0', 'C', '0', '0', 'workflow:process:migrate', 'Promotion', 'admin', '2026-10-16 08:53:07', NULL, NULL, '运行中流程实例迁移到新流程定义（人事变动/版本切换）', NULL, NULL, 'Process Migrate', '流程遷移');
INSERT INTO basicdb_flowable.sys_menu VALUES (1955896250820567047, '清空全部流程数据', 1511, 10, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:clearAll', '#', 'admin', '2026-10-16 09:44:16', NULL, NULL, '危险操作：清空流程实例/定义/部署/模型与全部业务流程表', NULL, NULL, 'Clear All Process Data', '清空全部流程數據');


--
-- TOC entry 4427 (class 0 OID 4483786)
-- Dependencies: 304
-- Data for Name: sys_notice; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--



--
-- TOC entry 4495 (class 0 OID 4487127)
-- Dependencies: 372
-- Data for Name: sys_notice_read; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4429 (class 0 OID 4483793)
-- Dependencies: 306
-- Data for Name: sys_oper_log; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_oper_log VALUES (379, '操作日志', 9, 'com.basic.web.controller.monitor.SysOperlogController.clean()', 'DELETE', 1, 'admin', NULL, '/monitor/operlog/clean', '127.0.0.1', '内网IP', '{}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 14:17:16');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (380, '登录日志', 9, 'com.basic.web.controller.monitor.SysLogininforController.clean()', 'DELETE', 1, 'admin', NULL, '/monitor/logininfor/clean', '127.0.0.1', '内网IP', '{}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 14:17:20');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (381, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1754778882690195458', '127.0.0.1', '内网IP', '{menuId=1754778882690195458}', '{"msg":"存在子菜单,不允许删除","code":500}', 0, NULL, '2026-10-14 14:17:33');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (382, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1064', '127.0.0.1', '内网IP', '{menuId=1064}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 14:17:41');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (383, '流程分类', 1, 'com.basic.workflow.controller.WfCategoryController.add()', 'POST', 1, 'admin', NULL, '/workflow/category', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":"admin","createTime":"2026-10-14 16:47:13","updateBy":null,"updateTime":null,"remark":null,"params":{},"categoryId":"2110291321662844929","categoryName":"测试流程引擎","code":"testflowable","parent":0,"delFlag":"0","codes":null,"organizationIds":null,"financeagencyIds":null,"url":null}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 16:47:13');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (384, '流程分类', 1, 'com.basic.workflow.controller.WfCategoryController.add()', 'POST', 1, 'admin', NULL, '/workflow/category', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":"admin","createTime":"2026-10-14 17:02:34","updateBy":null,"updateTime":null,"remark":null,"params":{},"categoryId":"2110295187640926209","categoryName":"测试流程2","code":"testflowable2","parent":"2110291321662844929","delFlag":"0","codes":null,"organizationIds":null,"financeagencyIds":null,"url":null}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 17:02:35');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (385, '流程分类', 3, 'com.basic.workflow.controller.WfCategoryController.remove()', 'DELETE', 1, 'admin', NULL, '/workflow/category/2110295187640926209', '127.0.0.1', '内网IP', '{categoryIds=2110295187640926209}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 17:06:08');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (386, '流程分类', 1, 'com.basic.workflow.controller.WfCategoryController.add()', 'POST', 1, 'admin', NULL, '/workflow/category', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":"admin","createTime":"2026-10-14 17:06:27","updateBy":null,"updateTime":null,"remark":null,"params":{},"categoryId":"2110296164800438274","categoryName":"测试流程2","code":"testflowable2","parent":"2110291321662844929","delFlag":"0","codes":null,"organizationIds":null,"financeagencyIds":null,"url":null}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 17:06:28');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (387, '菜单管理', 2, 'com.basic.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":null,"createTime":"2026-10-14 16:31:42","updateBy":"admin","updateTime":null,"remark":null,"params":{},"menuId":1500,"menuName":"流程引擎","parentName":null,"parentId":0,"orderNum":5,"path":"workflow","component":null,"query":null,"isFrame":"1","isCache":"0","menuType":"M","visible":"0","status":"0","perms":"","icon":"tree","children":[]}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-15 08:40:09');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (388, '流程分类', 3, 'com.basic.workflow.controller.WfCategoryController.remove()', 'DELETE', 1, 'admin', NULL, '/workflow/category/2110296164800438274', '127.0.0.1', '内网IP', '{categoryIds=2110296164800438274}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-15 09:15:45');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (389, '流程分类', 3, 'com.basic.workflow.controller.WfCategoryController.remove()', 'DELETE', 1, 'admin', NULL, '/workflow/category/2110291321662844929', '127.0.0.1', '内网IP', '{categoryIds=2110291321662844929}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-15 09:15:47');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (390, '流程分类', 1, 'com.basic.workflow.controller.WfCategoryController.add()', 'POST', 1, 'admin', NULL, '/workflow/category', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":"admin","createTime":"2026-10-15 09:21:58","updateBy":null,"updateTime":null,"remark":null,"params":{},"categoryId":"2110541658743197697","categoryName":"测试","code":"cs","parent":0,"delFlag":"0","codes":null,"url":null}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-15 09:21:58');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (391, '流程达式', 5, 'com.basic.workflow.controller.WfExpressionController.export()', 'POST', 1, 'admin', NULL, '/workflow/expression/export', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":null,"createTime":null,"updateBy":null,"updateTime":null,"remark":null,"params":{},"id":null,"name":null,"expression":null,"status":null,"type":null}', NULL, 0, NULL, '2026-10-15 10:03:00');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (392, '字典类型', 9, 'com.basic.web.controller.system.SysDictTypeController.refreshCache()', 'DELETE', 1, 'admin', NULL, '/system/dict/type/refreshCache', '127.0.0.1', '内网IP', '{}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-15 14:09:05');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (393, '用户管理', 2, 'com.basic.web.controller.system.SysUserController.resetPwd()', 'PUT', 1, 'admin', NULL, '/system/user/resetPwd', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":null,"createTime":null,"updateBy":"admin","updateTime":null,"remark":null,"params":{},"userId":5,"deptId":null,"userName":null,"nickName":null,"email":null,"phonenumber":null,"sex":null,"avatar":null,"password":"$2a$10$Dr57Zwq8p4Zbo9yTGBBsL.crFeovrvMGhZcz/eom0yXAdxfYkPxnm","salt":null,"status":null,"delFlag":null,"loginIp":null,"loginDate":null,"dept":null,"roles":null,"roleIds":null,"postIds":null,"roleId":null,"admin":false}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-15 14:21:40');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (394, '用户管理', 2, 'com.basic.web.controller.system.SysUserController.edit()', 'PUT', 1, 'admin', NULL, '/system/user', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":"admin","createTime":"2024-01-26 15:10:01","updateBy":"admin","updateTime":null,"remark":"系统管理员","params":{},"userId":5,"deptId":100,"userName":"xiaoxi","nickName":"系统管理员","email":"xiaoxi@email.com","phonenumber":"13333333333","sex":"0","avatar":null,"password":"","salt":null,"status":"0","delFlag":"0","loginIp":"127.0.0.1","loginDate":"2026-10-15 14:21:53","dept":{"searchValue":null,"createBy":null,"createTime":null,"updateBy":null,"updateTime":null,"remark":null,"params":{},"deptId":100,"parentId":0,"ancestors":"0","deptName":"集团公司","orderNum":0,"leader":"管理员","phone":null,"email":null,"status":"0","delFlag":null,"parentName":null,"children":[]},"roles":[{"searchValue":null,"createBy":null,"createTime":null,"updateBy":null,"updateTime":null,"remark":null,"params":{},"roleId":2,"roleName":"普通角色","roleKey":"common","roleSort":2,"dataScope":"2","menuCheckStrictly":null,"deptCheckStrictly":null,"status":"0","delFlag":null,"flag":false,"menuIds":null,"deptIds":null,"admin":false}],"roleIds":[2],"postIds":[],"roleId":null,"admin":false}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-15 14:22:26');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (395, '角色管理', 2, 'com.basic.web.controller.system.SysRoleController.edit()', 'PUT', 1, 'admin', NULL, '/system/role', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":null,"createTime":"2023-02-28 17:09:30","updateBy":"admin","updateTime":null,"remark":"普通角色","params":{},"roleId":2,"roleName":"普通角色","roleKey":"common","roleSort":2,"dataScope":"2","menuCheckStrictly":true,"deptCheckStrictly":true,"status":"0","delFlag":"0","flag":false,"menuIds":[1,105,3,100,1001,1002,1003,1004,1005,1006,1007,101,1008,1009,1010,1011,1012,102,1013,1014,1015,1016,103,1017,1018,1019,1020,104,1021,1022,1023,1024,1025,1026,106,107,108,500,501,2,114,115,116,111,109,110,112,113,1500,1511,1501,1570,1571,1572,1573,1574,1575,1576,1502,1503,1577,1504,1505,1506,1601,1530,1602,1531,1603,1532,1533,1507,1540,1541,1542,1543,1544,1615,1616,1617,1618,1619,1620,1621,1622,1623,1624,1625,1509,1560,1561,1562,1563,1608,1609,1610,1611,1612,1613,1614,1678,1679,1680,1681,1510,1580,1581,1582,1583,1606,1607,1699,1626,1627,1628,1629,1630,1631,1632,1633,1634,1635,1636,1637,1638,1639,1640,1641,1642,1643,1644,1645,1646,1647,1648,1649,1650,1651,1652,1653,1654,1655,1656,1657,1658,1659,1660,1661,1662,1663,1664,1665,1666,1667,1668,1669,1670,1671,1672,1673,1674,1675,1676,1677,1682,1683,1684,1685,1686,1687,1688,1689,1690,1691],"deptIds":null,"admin":false}', NULL, 1, '
### Error updating database.  Cause: org.postgresql.util.PSQLException: ERROR: column "menu_check_strictly" is of type smallint but expression is of type boolean
  建议：You will need to rewrite or cast the expression.
  位置：127
### The error may exist in file [D:\idea\agent-keycloak\base-postgresql-vue3\basic-admin-core\target\classes\mapper\system\SysRoleMapper.xml]
### The error may involve com.basic.system.mapper.SysRoleMapper.updateRole-Inline
### The error occurred while setting parameters
### SQL: update sys_role     SET role_name = ?,     role_key = ?,     role_sort = ?,     data_scope = ?,     menu_check_strictly = ?,     dept_check_strictly = ?,     status = ?,     remark = ?,     update_by = ?,     update_time = now()     where role_id = ?
### Cause: org.postgresql.util.PSQLException: ERROR: column "menu_check_strictly" is of type smallint but expression is of type boolean
  建议：You will need to rewrite or cast the expression.
  位置：127
; bad SQL grammar []; nested exception is org.postgresql.util.PSQLException: ERROR: column "menu_check_strictly" is of type smallint but expression is of type boolean
  建议：You will need to rewrite or cast the expression.
  位置：127', '2026-10-15 14:22:37');
INSERT INTO basicdb_flowable.sys_oper_log VALUES (396, '角色管理', 2, 'com.basic.web.controller.system.SysRoleController.edit()', 'PUT', 1, 'admin', NULL, '/system/role', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":null,"createTime":"2023-02-28 17:09:30","updateBy":"admin","updateTime":null,"remark":"普通角色","params":{},"roleId":2,"roleName":"普通角色","roleKey":"common","roleSort":2,"dataScope":"2","menuCheckStrictly":true,"deptCheckStrictly":true,"status":"0","delFlag":"0","flag":false,"menuIds":[1,105,3,100,1001,1002,1003,1004,1005,1006,1007,101,1008,1009,1010,1011,1012,102,1013,1014,1015,1016,103,1017,1018,1019,1020,104,1021,1022,1023,1024,1025,1026,106,107,108,500,501,2,114,115,116,111,109,110,112,113,1500,1511,1501,1570,1571,1572,1573,1574,1575,1576,1502,1503,1577,1504,1505,1506,1601,1530,1602,1531,1603,1532,1533,1507,1540,1541,1542,1543,1544,1615,1616,1617,1618,1619,1620,1621,1622,1623,1624,1625,1509,1560,1561,1562,1563,1608,1609,1610,1611,1612,1613,1614,1678,1679,1680,1681,1510,1580,1581,1582,1583,1606,1607,1699,1626,1627,1628,1629,1630,1631,1632,1633,1634,1635,1636,1637,1638,1639,1640,1641,1642,1643,1644,1645,1646,1647,1648,1649,1650,1651,1652,1653,1654,1655,1656,1657,1658,1659,1660,1661,1662,1663,1664,1665,1666,1667,1668,1669,1670,1671,1672,1673,1674,1675,1676,1677,1682,1683,1684,1685,1686,1687,1688,1689,1690,1691],"deptIds":null,"admin":false}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-15 14:24:56');


--
-- TOC entry 4431 (class 0 OID 4483802)
-- Dependencies: 308
-- Data for Name: sys_post; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_post VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2023-02-28 17:09:30', '', NULL, '');


--
-- TOC entry 4433 (class 0 OID 4483808)
-- Dependencies: 310
-- Data for Name: sys_role; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_role VALUES (1, '超级管理员', 'admin', 1, '1', true, true, '0', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '超级管理员');
INSERT INTO basicdb_flowable.sys_role VALUES (4, '333', 'hgf', 0, '1', true, true, '0', '2', 'admin', '2024-02-06 14:00:18', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_role VALUES (5, '用户1', 'usss', 3, '1', true, true, '0', '2', 'admin', '2024-02-06 16:52:25', NULL, NULL, NULL);
INSERT INTO basicdb_flowable.sys_role VALUES (2, '普通角色', 'common', 2, '2', true, true, '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2026-10-15 14:24:56', '普通角色');


--
-- TOC entry 4434 (class 0 OID 4483817)
-- Dependencies: 311
-- Data for Name: sys_role_dept; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_role_dept VALUES (2, 100);
INSERT INTO basicdb_flowable.sys_role_dept VALUES (2, 101);
INSERT INTO basicdb_flowable.sys_role_dept VALUES (2, 105);


--
-- TOC entry 4435 (class 0 OID 4483820)
-- Dependencies: 312
-- Data for Name: sys_role_menu; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1561);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1500);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1501);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1502);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1503);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1504);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1505);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1506);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1507);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1509);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1510);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1530);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1531);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1532);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1533);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1540);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1541);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1542);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1543);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1544);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1560);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1562);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1563);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1570);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1571);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1572);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1573);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1574);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1575);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1576);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1577);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1580);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1581);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1582);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1583);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1699);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1511);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 105);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 3);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 100);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1001);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1002);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1003);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1004);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1005);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1006);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1007);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 101);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1008);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1009);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1010);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1011);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1012);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 102);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1013);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1014);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1015);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1016);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 103);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1017);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1018);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1019);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1020);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 104);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1021);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1022);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1023);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1024);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1025);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1026);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 106);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 107);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 108);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 500);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 501);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 2);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 114);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 115);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 116);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 111);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 109);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 110);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 112);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 113);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1500);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1511);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1501);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1570);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1571);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1572);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1573);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1574);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1575);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1576);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1502);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1503);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1577);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1504);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1505);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1506);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1601);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1530);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1602);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1531);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1603);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1532);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1533);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1507);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1540);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1541);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1542);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1543);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1544);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1615);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1616);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1617);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1618);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1619);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1620);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1621);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1622);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1623);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1624);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1625);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1509);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1560);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1561);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1562);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1563);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1608);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1609);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1610);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1611);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1612);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1613);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1614);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1678);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1679);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1680);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1681);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1510);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1580);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1581);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1582);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1583);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1606);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1607);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1699);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1626);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1627);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1628);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1629);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1630);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1631);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1632);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1633);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1634);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1635);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1636);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1637);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1638);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1639);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1640);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1641);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1642);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1643);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1644);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1645);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1646);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1647);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1648);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1649);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1650);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1651);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1652);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1653);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1654);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1655);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1656);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1657);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1658);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1659);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1660);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1661);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1662);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1663);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1664);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1665);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1666);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1667);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1668);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1669);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1670);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1671);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1672);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1673);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1674);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1675);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1676);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1677);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1682);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1683);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1684);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1685);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1686);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1687);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1688);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1689);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1690);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (2, 1691);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1955896250820567042);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1955896250820567043);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1955896250820567044);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1955896250820567045);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1955896250820567046);
INSERT INTO basicdb_flowable.sys_role_menu VALUES (1, 1955896250820567047);


--
-- TOC entry 4437 (class 0 OID 4483824)
-- Dependencies: 314
-- Data for Name: sys_user; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_user VALUES (9, NULL, 'test002', '测试2', '00', NULL, NULL, '0', NULL, '$2a$10$udfBxRm0omFl7XpSmBk7/.wfsRfzP5qqc0EJ6bM0s01fgmoSiDIBq', '0', '0', '127.0.0.1', '2024-02-21 17:18:01', 'admin', '2024-02-21 17:17:44', NULL, '2024-02-21 17:18:00', NULL, NULL);
INSERT INTO basicdb_flowable.sys_user VALUES (4, 101, 'xiaoan', '安全管理员', '00', 'xiaoan@email.com', '19999999999', '0', '/profile/avatar/2024/04/22/blob_20240422160106A001.jpeg', '$2a$10$So1TNdFpzE1dB09Z0eQs1OzFpFLMrWxbYtfTvxEFLMC/8SXTUPPEK', '0', '0', '127.0.0.1', '2024-10-11 10:13:43', 'admin', '2024-01-26 15:07:37', 'admin', '2024-10-11 10:13:42', '安全管理员', '2024-10-11 10:13:27');
INSERT INTO basicdb_flowable.sys_user VALUES (6, NULL, '13249230230', 'AA', '00', NULL, NULL, '0', NULL, '$2a$10$.3MLA6zZE6RPDLzSx6jzseXhe9ShBXBgddJ5Z09Bp1immgkZ978JS', '0', '0', '192.168.170.21', '2024-02-06 15:51:51', 'admin', '2024-01-26 15:24:02', 'admin', '2024-02-06 15:51:50', NULL, '2024-02-21 11:49:28');
INSERT INTO basicdb_flowable.sys_user VALUES (7, NULL, '用户1', 'user1', '00', NULL, NULL, '0', NULL, '$2a$10$wfJ4zAYQ0kaZyp2XRyLO7.5bnloK3/M0HiOatf3kRWt//u2KMUNdu', '0', '2', NULL, NULL, 'admin', '2024-02-06 16:51:53', NULL, NULL, NULL, '2024-02-21 11:49:28');
INSERT INTO basicdb_flowable.sys_user VALUES (2, 105, 'xiaoshen', '审计管理员', '00', 'xiaoshen@qq.com', '17777777777', '1', '/profile/avatar/2024/02/06/blob_20240206154427A003.jpeg', '$2a$10$MpBkNRsOzwiy54C/Vx8JoOjk6NV3Zz.jws030m1DMvguCcPP4OLJ6', '0', '0', '192.168.170.21', '2024-02-07 08:21:20', 'admin', '2023-02-28 17:09:30', 'admin', '2024-02-07 08:21:19', '审计管理员', '2024-02-21 11:49:28');
INSERT INTO basicdb_flowable.sys_user VALUES (8, NULL, 'test001', '测试', '00', NULL, NULL, '0', NULL, '$2a$10$RAs8Dzmmt.UUD4JZvLX4Wes1Eblh0Z/OpQceqseQk78zhNvI62XvK', '0', '0', '127.0.0.1', '2025-08-14 10:43:36', 'admin', '2024-02-21 17:09:51', 'admin', '2025-08-14 10:43:36', NULL, '2025-08-14 10:43:21');
INSERT INTO basicdb_flowable.sys_user VALUES (5, 100, 'xiaoxi', '系统管理员', '00', 'xiaoxi@email.com', '13333333333', '0', NULL, '$2a$10$Dr57Zwq8p4Zbo9yTGBBsL.crFeovrvMGhZcz/eom0yXAdxfYkPxnm', '0', '0', '127.0.0.1', '2026-10-15 16:43:12', 'admin', '2024-01-26 15:10:01', 'admin', '2026-10-15 16:43:12', '系统管理员', '2024-04-22 16:03:29');
INSERT INTO basicdb_flowable.sys_user VALUES (1, 100, 'admin', '超*管', '00', 'xiaochao@163.com', '18888888888', '1', '/profile/avatar/2024/02/06/blob_20240206154306A002.jpeg', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2026-10-16 08:55:27', 'admin', '2023-02-28 17:09:30', '', '2026-10-16 08:55:28', '管理员', '2024-02-21 11:49:28');


--
-- TOC entry 4438 (class 0 OID 4483833)
-- Dependencies: 315
-- Data for Name: sys_user_post; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_user_post VALUES (1, 1);


--
-- TOC entry 4439 (class 0 OID 4483836)
-- Dependencies: 316
-- Data for Name: sys_user_role; Type: TABLE DATA; Schema: basicdb_flowable; Owner: postgres
--

INSERT INTO basicdb_flowable.sys_user_role VALUES (1, 1);
INSERT INTO basicdb_flowable.sys_user_role VALUES (4, 2);
INSERT INTO basicdb_flowable.sys_user_role VALUES (2, 2);
INSERT INTO basicdb_flowable.sys_user_role VALUES (8, 2);
INSERT INTO basicdb_flowable.sys_user_role VALUES (9, 2);
INSERT INTO basicdb_flowable.sys_user_role VALUES (5, 2);


--
-- TOC entry 4450 (class 0 OID 4484065)
-- Dependencies: 327
-- Data for Name: tbl_flow_extend_procinst; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4441 (class 0 OID 4483921)
-- Dependencies: 318
-- Data for Name: wf_category; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4451 (class 0 OID 4484092)
-- Dependencies: 328
-- Data for Name: wf_category_org_link; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4448 (class 0 OID 4484030)
-- Dependencies: 325
-- Data for Name: wf_comment_info; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4447 (class 0 OID 4484009)
-- Dependencies: 324
-- Data for Name: wf_copy; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4445 (class 0 OID 4483975)
-- Dependencies: 322
-- Data for Name: wf_deploy_form; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4442 (class 0 OID 4483936)
-- Dependencies: 319
-- Data for Name: wf_form; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4444 (class 0 OID 4483964)
-- Dependencies: 321
-- Data for Name: wf_form_category; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4443 (class 0 OID 4483952)
-- Dependencies: 320
-- Data for Name: wf_form_template; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4446 (class 0 OID 4483991)
-- Dependencies: 323
-- Data for Name: wf_task_form; Type: TABLE DATA; Schema: basicdb_flowable; Owner: system
--



--
-- TOC entry 4730 (class 0 OID 0)
-- Dependencies: 360
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: system
--

SELECT pg_catalog.setval('basicdb_flowable.act_evt_log_log_nr__seq', 1, false);


--
-- TOC entry 4731 (class 0 OID 0)
-- Dependencies: 344
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: system
--

SELECT pg_catalog.setval('basicdb_flowable.act_hi_tsk_log_id__seq', 1, false);


--
-- TOC entry 4732 (class 0 OID 0)
-- Dependencies: 270
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.gen_table_column_column_id_seq', 185, true);


--
-- TOC entry 4733 (class 0 OID 0)
-- Dependencies: 271
-- Name: gen_table_table_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: system
--

SELECT pg_catalog.setval('basicdb_flowable.gen_table_table_id_seq', 18, true);


--
-- TOC entry 4734 (class 0 OID 0)
-- Dependencies: 286
-- Name: sys_config_config_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_config_config_id_seq', 13, true);


--
-- TOC entry 4735 (class 0 OID 0)
-- Dependencies: 289
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_dept_dept_id_seq', 111, true);


--
-- TOC entry 4736 (class 0 OID 0)
-- Dependencies: 291
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_dict_data_dict_code_seq', 57, true);


--
-- TOC entry 4737 (class 0 OID 0)
-- Dependencies: 293
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_dict_type_dict_id_seq', 19, true);


--
-- TOC entry 4738 (class 0 OID 0)
-- Dependencies: 297
-- Name: sys_job_job_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_job_job_id_seq', 4, true);


--
-- TOC entry 4739 (class 0 OID 0)
-- Dependencies: 299
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_job_log_job_log_id_seq', 1, false);


--
-- TOC entry 4740 (class 0 OID 0)
-- Dependencies: 301
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_logininfor_info_id_seq', 741, true);


--
-- TOC entry 4741 (class 0 OID 0)
-- Dependencies: 303
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_menu_menu_id_seq', 1066, true);


--
-- TOC entry 4742 (class 0 OID 0)
-- Dependencies: 305
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_notice_notice_id_seq', 3, true);


--
-- TOC entry 4743 (class 0 OID 0)
-- Dependencies: 373
-- Name: sys_notice_read_read_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: system
--

SELECT pg_catalog.setval('basicdb_flowable.sys_notice_read_read_id_seq', 1, false);


--
-- TOC entry 4744 (class 0 OID 0)
-- Dependencies: 307
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_oper_log_oper_id_seq', 396, true);


--
-- TOC entry 4745 (class 0 OID 0)
-- Dependencies: 309
-- Name: sys_post_post_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_post_post_id_seq', 5, true);


--
-- TOC entry 4746 (class 0 OID 0)
-- Dependencies: 313
-- Name: sys_role_role_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_role_role_id_seq', 5, true);


--
-- TOC entry 4747 (class 0 OID 0)
-- Dependencies: 317
-- Name: sys_user_user_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable.sys_user_user_id_seq', 9, true);


--
-- TOC entry 3883 (class 2606 OID 4483856)
-- Name: gen_table PRIMARY; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.gen_table
    ADD CONSTRAINT "PRIMARY" PRIMARY KEY (table_id);


--
-- TOC entry 3885 (class 2606 OID 4483858)
-- Name: gen_table_column PRIMARY00000; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.gen_table_column
    ADD CONSTRAINT "PRIMARY00000" PRIMARY KEY (column_id);


--
-- TOC entry 3887 (class 2606 OID 4483860)
-- Name: qrtz_blob_triggers PRIMARY00001; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.qrtz_blob_triggers
    ADD CONSTRAINT "PRIMARY00001" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 3889 (class 2606 OID 4483862)
-- Name: qrtz_calendars PRIMARY00002; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.qrtz_calendars
    ADD CONSTRAINT "PRIMARY00002" PRIMARY KEY (sched_name, calendar_name);


--
-- TOC entry 3891 (class 2606 OID 4483864)
-- Name: qrtz_cron_triggers PRIMARY00003; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.qrtz_cron_triggers
    ADD CONSTRAINT "PRIMARY00003" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 3893 (class 2606 OID 4483866)
-- Name: qrtz_fired_triggers PRIMARY00004; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.qrtz_fired_triggers
    ADD CONSTRAINT "PRIMARY00004" PRIMARY KEY (sched_name, entry_id);


--
-- TOC entry 3895 (class 2606 OID 4483868)
-- Name: qrtz_job_details PRIMARY00005; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.qrtz_job_details
    ADD CONSTRAINT "PRIMARY00005" PRIMARY KEY (sched_name, job_name, job_group);


--
-- TOC entry 3897 (class 2606 OID 4483870)
-- Name: qrtz_locks PRIMARY00006; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.qrtz_locks
    ADD CONSTRAINT "PRIMARY00006" PRIMARY KEY (sched_name, lock_name);


--
-- TOC entry 3899 (class 2606 OID 4483872)
-- Name: qrtz_paused_trigger_grps PRIMARY00007; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.qrtz_paused_trigger_grps
    ADD CONSTRAINT "PRIMARY00007" PRIMARY KEY (sched_name, trigger_group);


--
-- TOC entry 3901 (class 2606 OID 4483874)
-- Name: qrtz_scheduler_state PRIMARY00008; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.qrtz_scheduler_state
    ADD CONSTRAINT "PRIMARY00008" PRIMARY KEY (sched_name, instance_name);


--
-- TOC entry 3903 (class 2606 OID 4483876)
-- Name: qrtz_simple_triggers PRIMARY00009; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.qrtz_simple_triggers
    ADD CONSTRAINT "PRIMARY00009" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 3905 (class 2606 OID 4483878)
-- Name: qrtz_simprop_triggers PRIMARY00010; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.qrtz_simprop_triggers
    ADD CONSTRAINT "PRIMARY00010" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 3907 (class 2606 OID 4483880)
-- Name: qrtz_triggers PRIMARY00011; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.qrtz_triggers
    ADD CONSTRAINT "PRIMARY00011" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 3910 (class 2606 OID 4483882)
-- Name: sys_config PRIMARY00012; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_config
    ADD CONSTRAINT "PRIMARY00012" PRIMARY KEY (config_id);


--
-- TOC entry 3914 (class 2606 OID 4483884)
-- Name: sys_dept PRIMARY00013; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_dept
    ADD CONSTRAINT "PRIMARY00013" PRIMARY KEY (dept_id);


--
-- TOC entry 3916 (class 2606 OID 4483886)
-- Name: sys_dict_data PRIMARY00014; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_dict_data
    ADD CONSTRAINT "PRIMARY00014" PRIMARY KEY (dict_code);


--
-- TOC entry 3918 (class 2606 OID 4483888)
-- Name: sys_dict_type PRIMARY00015; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_dict_type
    ADD CONSTRAINT "PRIMARY00015" PRIMARY KEY (dict_id);


--
-- TOC entry 3926 (class 2606 OID 4483890)
-- Name: sys_job PRIMARY00016; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_job
    ADD CONSTRAINT "PRIMARY00016" PRIMARY KEY (job_id, job_name, job_group);


--
-- TOC entry 3928 (class 2606 OID 4483892)
-- Name: sys_job_log PRIMARY00017; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_job_log
    ADD CONSTRAINT "PRIMARY00017" PRIMARY KEY (job_log_id);


--
-- TOC entry 3930 (class 2606 OID 4483894)
-- Name: sys_logininfor PRIMARY00018; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_logininfor
    ADD CONSTRAINT "PRIMARY00018" PRIMARY KEY (info_id);


--
-- TOC entry 3932 (class 2606 OID 4483896)
-- Name: sys_menu PRIMARY00019; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_menu
    ADD CONSTRAINT "PRIMARY00019" PRIMARY KEY (menu_id);


--
-- TOC entry 3934 (class 2606 OID 4483898)
-- Name: sys_notice PRIMARY00020; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_notice
    ADD CONSTRAINT "PRIMARY00020" PRIMARY KEY (notice_id);


--
-- TOC entry 3936 (class 2606 OID 4483900)
-- Name: sys_oper_log PRIMARY00021; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_oper_log
    ADD CONSTRAINT "PRIMARY00021" PRIMARY KEY (oper_id);


--
-- TOC entry 3938 (class 2606 OID 4483902)
-- Name: sys_post PRIMARY00022; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_post
    ADD CONSTRAINT "PRIMARY00022" PRIMARY KEY (post_id);


--
-- TOC entry 3940 (class 2606 OID 4483904)
-- Name: sys_role PRIMARY00023; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_role
    ADD CONSTRAINT "PRIMARY00023" PRIMARY KEY (role_id);


--
-- TOC entry 3942 (class 2606 OID 4483906)
-- Name: sys_role_dept PRIMARY00024; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_role_dept
    ADD CONSTRAINT "PRIMARY00024" PRIMARY KEY (role_id, dept_id);


--
-- TOC entry 3944 (class 2606 OID 4483908)
-- Name: sys_role_menu PRIMARY00025; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_role_menu
    ADD CONSTRAINT "PRIMARY00025" PRIMARY KEY (role_id, menu_id);


--
-- TOC entry 3946 (class 2606 OID 4483910)
-- Name: sys_user PRIMARY00026; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_user
    ADD CONSTRAINT "PRIMARY00026" PRIMARY KEY (user_id);


--
-- TOC entry 3948 (class 2606 OID 4483912)
-- Name: sys_user_post PRIMARY00027; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_user_post
    ADD CONSTRAINT "PRIMARY00027" PRIMARY KEY (user_id, post_id);


--
-- TOC entry 3950 (class 2606 OID 4483914)
-- Name: sys_user_role PRIMARY00028; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable.sys_user_role
    ADD CONSTRAINT "PRIMARY00028" PRIMARY KEY (user_id, role_id);


--
-- TOC entry 4196 (class 2606 OID 4485017)
-- Name: act_de_model_history act_de_model_history_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_de_model_history
    ADD CONSTRAINT act_de_model_history_pkey PRIMARY KEY (id);


--
-- TOC entry 4191 (class 2606 OID 4485007)
-- Name: act_de_model act_de_model_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_de_model
    ADD CONSTRAINT act_de_model_pkey PRIMARY KEY (id);


--
-- TOC entry 4199 (class 2606 OID 4485025)
-- Name: act_de_model_relation act_de_model_relation_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_de_model_relation
    ADD CONSTRAINT act_de_model_relation_pkey PRIMARY KEY (id);


--
-- TOC entry 4150 (class 2606 OID 4484476)
-- Name: act_evt_log act_evt_log_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_evt_log
    ADD CONSTRAINT act_evt_log_pkey PRIMARY KEY (log_nr_);


--
-- TOC entry 3987 (class 2606 OID 4484126)
-- Name: act_ge_bytearray act_ge_bytearray_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ge_bytearray
    ADD CONSTRAINT act_ge_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 3985 (class 2606 OID 4484119)
-- Name: act_ge_property act_ge_property_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ge_property
    ADD CONSTRAINT act_ge_property_pkey PRIMARY KEY (name_);


--
-- TOC entry 4174 (class 2606 OID 4484710)
-- Name: act_hi_actinst act_hi_actinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_hi_actinst
    ADD CONSTRAINT act_hi_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4189 (class 2606 OID 4484731)
-- Name: act_hi_attachment act_hi_attachment_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_hi_attachment
    ADD CONSTRAINT act_hi_attachment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4187 (class 2606 OID 4484724)
-- Name: act_hi_comment act_hi_comment_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_hi_comment
    ADD CONSTRAINT act_hi_comment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4180 (class 2606 OID 4484717)
-- Name: act_hi_detail act_hi_detail_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_hi_detail
    ADD CONSTRAINT act_hi_detail_pkey PRIMARY KEY (id_);


--
-- TOC entry 4026 (class 2606 OID 4484207)
-- Name: act_hi_entitylink act_hi_entitylink_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_hi_entitylink
    ADD CONSTRAINT act_hi_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4012 (class 2606 OID 4484185)
-- Name: act_hi_identitylink act_hi_identitylink_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_hi_identitylink
    ADD CONSTRAINT act_hi_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4167 (class 2606 OID 4484699)
-- Name: act_hi_procinst act_hi_procinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4169 (class 2606 OID 4484701)
-- Name: act_hi_procinst act_hi_procinst_proc_inst_id__key; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_proc_inst_id__key UNIQUE (proc_inst_id_);


--
-- TOC entry 4046 (class 2606 OID 4484243)
-- Name: act_hi_taskinst act_hi_taskinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_hi_taskinst
    ADD CONSTRAINT act_hi_taskinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4052 (class 2606 OID 4484253)
-- Name: act_hi_tsk_log act_hi_tsk_log_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_hi_tsk_log
    ADD CONSTRAINT act_hi_tsk_log_pkey PRIMARY KEY (id_);


--
-- TOC entry 4062 (class 2606 OID 4484279)
-- Name: act_hi_varinst act_hi_varinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_hi_varinst
    ADD CONSTRAINT act_hi_varinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4154 (class 2606 OID 4484481)
-- Name: act_procdef_info act_procdef_info_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_procdef_info
    ADD CONSTRAINT act_procdef_info_pkey PRIMARY KEY (id_);


--
-- TOC entry 4130 (class 2606 OID 4484441)
-- Name: act_re_deployment act_re_deployment_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_re_deployment
    ADD CONSTRAINT act_re_deployment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4135 (class 2606 OID 4484449)
-- Name: act_re_model act_re_model_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_re_model
    ADD CONSTRAINT act_re_model_pkey PRIMARY KEY (id_);


--
-- TOC entry 4146 (class 2606 OID 4484466)
-- Name: act_re_procdef act_re_procdef_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_re_procdef
    ADD CONSTRAINT act_re_procdef_pkey PRIMARY KEY (id_);


--
-- TOC entry 4165 (class 2606 OID 4484490)
-- Name: act_ru_actinst act_ru_actinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_actinst
    ADD CONSTRAINT act_ru_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4113 (class 2606 OID 4484314)
-- Name: act_ru_deadletter_job act_ru_deadletter_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_deadletter_job
    ADD CONSTRAINT act_ru_deadletter_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4024 (class 2606 OID 4484196)
-- Name: act_ru_entitylink act_ru_entitylink_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_entitylink
    ADD CONSTRAINT act_ru_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4035 (class 2606 OID 4484219)
-- Name: act_ru_event_subscr act_ru_event_subscr_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_event_subscr
    ADD CONSTRAINT act_ru_event_subscr_pkey PRIMARY KEY (id_);


--
-- TOC entry 4144 (class 2606 OID 4484457)
-- Name: act_ru_execution act_ru_execution_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_execution
    ADD CONSTRAINT act_ru_execution_pkey PRIMARY KEY (id_);


--
-- TOC entry 4123 (class 2606 OID 4484330)
-- Name: act_ru_external_job act_ru_external_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_external_job
    ADD CONSTRAINT act_ru_external_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4115 (class 2606 OID 4484322)
-- Name: act_ru_history_job act_ru_history_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_history_job
    ADD CONSTRAINT act_ru_history_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4010 (class 2606 OID 4484173)
-- Name: act_ru_identitylink act_ru_identitylink_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_identitylink
    ADD CONSTRAINT act_ru_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4079 (class 2606 OID 4484290)
-- Name: act_ru_job act_ru_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_job
    ADD CONSTRAINT act_ru_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4102 (class 2606 OID 4484306)
-- Name: act_ru_suspended_job act_ru_suspended_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_suspended_job
    ADD CONSTRAINT act_ru_suspended_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4044 (class 2606 OID 4484230)
-- Name: act_ru_task act_ru_task_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_task
    ADD CONSTRAINT act_ru_task_pkey PRIMARY KEY (id_);


--
-- TOC entry 4091 (class 2606 OID 4484298)
-- Name: act_ru_timer_job act_ru_timer_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_timer_job
    ADD CONSTRAINT act_ru_timer_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4060 (class 2606 OID 4484263)
-- Name: act_ru_variable act_ru_variable_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_variable
    ADD CONSTRAINT act_ru_variable_pkey PRIMARY KEY (id_);


--
-- TOC entry 4156 (class 2606 OID 4484690)
-- Name: act_procdef_info act_uniq_info_procdef; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_procdef_info
    ADD CONSTRAINT act_uniq_info_procdef UNIQUE (proc_def_id_);


--
-- TOC entry 4148 (class 2606 OID 4484509)
-- Name: act_re_procdef act_uniq_procdef; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_re_procdef
    ADD CONSTRAINT act_uniq_procdef UNIQUE (key_, version_, derived_version_, tenant_id_);


--
-- TOC entry 4000 (class 2606 OID 4484165)
-- Name: flw_channel_definition flw_channel_definition_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.flw_channel_definition
    ADD CONSTRAINT flw_channel_definition_pkey PRIMARY KEY (id_);


--
-- TOC entry 3990 (class 2606 OID 4484131)
-- Name: flw_ev_databasechangeloglock flw_ev_databasechangeloglock_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.flw_ev_databasechangeloglock
    ADD CONSTRAINT flw_ev_databasechangeloglock_pkey PRIMARY KEY (id);


--
-- TOC entry 3997 (class 2606 OID 4484157)
-- Name: flw_event_definition flw_event_definition_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.flw_event_definition
    ADD CONSTRAINT flw_event_definition_pkey PRIMARY KEY (id_);


--
-- TOC entry 3992 (class 2606 OID 4484143)
-- Name: flw_event_deployment flw_event_deployment_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.flw_event_deployment
    ADD CONSTRAINT flw_event_deployment_pkey PRIMARY KEY (id_);


--
-- TOC entry 3994 (class 2606 OID 4484150)
-- Name: flw_event_resource flw_event_resource_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.flw_event_resource
    ADD CONSTRAINT flw_event_resource_pkey PRIMARY KEY (id_);


--
-- TOC entry 3976 (class 2606 OID 4484064)
-- Name: flw_expression flw_expression_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.flw_expression
    ADD CONSTRAINT flw_expression_pkey PRIMARY KEY (id);


--
-- TOC entry 4128 (class 2606 OID 4484427)
-- Name: flw_ru_batch_part flw_ru_batch_part_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.flw_ru_batch_part
    ADD CONSTRAINT flw_ru_batch_part_pkey PRIMARY KEY (id_);


--
-- TOC entry 4125 (class 2606 OID 4484419)
-- Name: flw_ru_batch flw_ru_batch_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.flw_ru_batch
    ADD CONSTRAINT flw_ru_batch_pkey PRIMARY KEY (id_);


--
-- TOC entry 3912 (class 2606 OID 4483916)
-- Name: sys_demo sys_demo_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.sys_demo
    ADD CONSTRAINT sys_demo_pkey PRIMARY KEY (id);


--
-- TOC entry 3924 (class 2606 OID 4483918)
-- Name: sys_files sys_files_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.sys_files
    ADD CONSTRAINT sys_files_pkey PRIMARY KEY (id);


--
-- TOC entry 4202 (class 2606 OID 4487132)
-- Name: sys_notice_read sys_notice_read_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.sys_notice_read
    ADD CONSTRAINT sys_notice_read_pkey PRIMARY KEY (read_id);


--
-- TOC entry 3979 (class 2606 OID 4486791)
-- Name: tbl_flow_extend_procinst tbl_flow_extend_procinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.tbl_flow_extend_procinst
    ADD CONSTRAINT tbl_flow_extend_procinst_pkey PRIMARY KEY (id);


--
-- TOC entry 3983 (class 2606 OID 4484098)
-- Name: wf_category_org_link wf_category_org_link_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.wf_category_org_link
    ADD CONSTRAINT wf_category_org_link_pkey PRIMARY KEY (id);


--
-- TOC entry 3952 (class 2606 OID 4483935)
-- Name: wf_category wf_category_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.wf_category
    ADD CONSTRAINT wf_category_pkey PRIMARY KEY (category_id);


--
-- TOC entry 3974 (class 2606 OID 4484049)
-- Name: wf_comment_info wf_comment_info_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.wf_comment_info
    ADD CONSTRAINT wf_comment_info_pkey PRIMARY KEY (id);


--
-- TOC entry 3970 (class 2606 OID 4484027)
-- Name: wf_copy wf_copy_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.wf_copy
    ADD CONSTRAINT wf_copy_pkey PRIMARY KEY (copy_id);


--
-- TOC entry 3962 (class 2606 OID 4486789)
-- Name: wf_deploy_form wf_deploy_form_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.wf_deploy_form
    ADD CONSTRAINT wf_deploy_form_pkey PRIMARY KEY (id);


--
-- TOC entry 3958 (class 2606 OID 4483974)
-- Name: wf_form_category wf_form_category_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.wf_form_category
    ADD CONSTRAINT wf_form_category_pkey PRIMARY KEY (id);


--
-- TOC entry 3954 (class 2606 OID 4483951)
-- Name: wf_form wf_form_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.wf_form
    ADD CONSTRAINT wf_form_pkey PRIMARY KEY (form_id);


--
-- TOC entry 3956 (class 2606 OID 4483963)
-- Name: wf_form_template wf_form_template_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.wf_form_template
    ADD CONSTRAINT wf_form_template_pkey PRIMARY KEY (form_id);


--
-- TOC entry 3966 (class 2606 OID 4486787)
-- Name: wf_task_form wf_task_form_pkey; Type: CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.wf_task_form
    ADD CONSTRAINT wf_task_form_pkey PRIMARY KEY (id);


--
-- TOC entry 4001 (class 1259 OID 4484540)
-- Name: act_idx_athrz_procedef; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_athrz_procedef ON basicdb_flowable.act_ru_identitylink USING btree (proc_def_id_);


--
-- TOC entry 3988 (class 1259 OID 4484495)
-- Name: act_idx_bytear_depl; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_bytear_depl ON basicdb_flowable.act_ge_bytearray USING btree (deployment_id_);


--
-- TOC entry 3998 (class 1259 OID 4484166)
-- Name: act_idx_channel_def_uniq; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE UNIQUE INDEX act_idx_channel_def_uniq ON basicdb_flowable.flw_channel_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 4197 (class 1259 OID 4485018)
-- Name: act_idx_de_model_history_model_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_de_model_history_model_id ON basicdb_flowable.act_de_model_history USING btree (model_id);


--
-- TOC entry 4192 (class 1259 OID 4485009)
-- Name: act_idx_de_model_key; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_de_model_key ON basicdb_flowable.act_de_model USING btree (model_key);


--
-- TOC entry 4200 (class 1259 OID 4485026)
-- Name: act_idx_de_model_relation_parent; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_de_model_relation_parent ON basicdb_flowable.act_de_model_relation USING btree (parent_model_id);


--
-- TOC entry 4193 (class 1259 OID 4485010)
-- Name: act_idx_de_model_tenant; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_de_model_tenant ON basicdb_flowable.act_de_model USING btree (tenant_id);


--
-- TOC entry 4194 (class 1259 OID 4485008)
-- Name: act_idx_de_model_type; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_de_model_type ON basicdb_flowable.act_de_model USING btree (model_type);


--
-- TOC entry 4103 (class 1259 OID 4484343)
-- Name: act_idx_deadletter_job_correlation_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_deadletter_job_correlation_id ON basicdb_flowable.act_ru_deadletter_job USING btree (correlation_id_);


--
-- TOC entry 4104 (class 1259 OID 4484342)
-- Name: act_idx_deadletter_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_deadletter_job_custom_values_id ON basicdb_flowable.act_ru_deadletter_job USING btree (custom_values_id_);


--
-- TOC entry 4105 (class 1259 OID 4484341)
-- Name: act_idx_deadletter_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_deadletter_job_exception_stack_id ON basicdb_flowable.act_ru_deadletter_job USING btree (exception_stack_id_);


--
-- TOC entry 4106 (class 1259 OID 4484636)
-- Name: act_idx_deadletter_job_execution_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_deadletter_job_execution_id ON basicdb_flowable.act_ru_deadletter_job USING btree (execution_id_);


--
-- TOC entry 4107 (class 1259 OID 4484648)
-- Name: act_idx_deadletter_job_proc_def_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_deadletter_job_proc_def_id ON basicdb_flowable.act_ru_deadletter_job USING btree (proc_def_id_);


--
-- TOC entry 4108 (class 1259 OID 4484642)
-- Name: act_idx_deadletter_job_process_instance_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_deadletter_job_process_instance_id ON basicdb_flowable.act_ru_deadletter_job USING btree (process_instance_id_);


--
-- TOC entry 4109 (class 1259 OID 4484406)
-- Name: act_idx_djob_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_djob_scope ON basicdb_flowable.act_ru_deadletter_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4110 (class 1259 OID 4484408)
-- Name: act_idx_djob_scope_def; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_djob_scope_def ON basicdb_flowable.act_ru_deadletter_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4111 (class 1259 OID 4484407)
-- Name: act_idx_djob_sub_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_djob_sub_scope ON basicdb_flowable.act_ru_deadletter_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4116 (class 1259 OID 4484409)
-- Name: act_idx_ejob_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ejob_scope ON basicdb_flowable.act_ru_external_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4117 (class 1259 OID 4484411)
-- Name: act_idx_ejob_scope_def; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ejob_scope_def ON basicdb_flowable.act_ru_external_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4118 (class 1259 OID 4484410)
-- Name: act_idx_ejob_sub_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ejob_sub_scope ON basicdb_flowable.act_ru_external_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4019 (class 1259 OID 4484198)
-- Name: act_idx_ent_lnk_ref_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ent_lnk_ref_scope ON basicdb_flowable.act_ru_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 4020 (class 1259 OID 4484199)
-- Name: act_idx_ent_lnk_root_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ent_lnk_root_scope ON basicdb_flowable.act_ru_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 4021 (class 1259 OID 4484197)
-- Name: act_idx_ent_lnk_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ent_lnk_scope ON basicdb_flowable.act_ru_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 4022 (class 1259 OID 4484200)
-- Name: act_idx_ent_lnk_scope_def; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ent_lnk_scope_def ON basicdb_flowable.act_ru_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 3995 (class 1259 OID 4484158)
-- Name: act_idx_event_def_uniq; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE UNIQUE INDEX act_idx_event_def_uniq ON basicdb_flowable.flw_event_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 4031 (class 1259 OID 4484221)
-- Name: act_idx_event_subscr; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_event_subscr ON basicdb_flowable.act_ru_event_subscr USING btree (execution_id_);


--
-- TOC entry 4032 (class 1259 OID 4484220)
-- Name: act_idx_event_subscr_config_; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_event_subscr_config_ ON basicdb_flowable.act_ru_event_subscr USING btree (configuration_);


--
-- TOC entry 4033 (class 1259 OID 4484222)
-- Name: act_idx_event_subscr_scoperef_; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_event_subscr_scoperef_ ON basicdb_flowable.act_ru_event_subscr USING btree (scope_id_, scope_type_);


--
-- TOC entry 4136 (class 1259 OID 4484516)
-- Name: act_idx_exe_parent; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_exe_parent ON basicdb_flowable.act_ru_execution USING btree (parent_id_);


--
-- TOC entry 4137 (class 1259 OID 4484528)
-- Name: act_idx_exe_procdef; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_exe_procdef ON basicdb_flowable.act_ru_execution USING btree (proc_def_id_);


--
-- TOC entry 4138 (class 1259 OID 4484510)
-- Name: act_idx_exe_procinst; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_exe_procinst ON basicdb_flowable.act_ru_execution USING btree (proc_inst_id_);


--
-- TOC entry 4139 (class 1259 OID 4484492)
-- Name: act_idx_exe_root; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_exe_root ON basicdb_flowable.act_ru_execution USING btree (root_proc_inst_id_);


--
-- TOC entry 4140 (class 1259 OID 4484522)
-- Name: act_idx_exe_super; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_exe_super ON basicdb_flowable.act_ru_execution USING btree (super_exec_);


--
-- TOC entry 4141 (class 1259 OID 4484491)
-- Name: act_idx_exec_buskey; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_exec_buskey ON basicdb_flowable.act_ru_execution USING btree (business_key_);


--
-- TOC entry 4142 (class 1259 OID 4484493)
-- Name: act_idx_exec_ref_id_; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_exec_ref_id_ ON basicdb_flowable.act_ru_execution USING btree (reference_id_);


--
-- TOC entry 4119 (class 1259 OID 4484346)
-- Name: act_idx_external_job_correlation_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_external_job_correlation_id ON basicdb_flowable.act_ru_external_job USING btree (correlation_id_);


--
-- TOC entry 4120 (class 1259 OID 4484345)
-- Name: act_idx_external_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_external_job_custom_values_id ON basicdb_flowable.act_ru_external_job USING btree (custom_values_id_);


--
-- TOC entry 4121 (class 1259 OID 4484344)
-- Name: act_idx_external_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_external_job_exception_stack_id ON basicdb_flowable.act_ru_external_job USING btree (exception_stack_id_);


--
-- TOC entry 4175 (class 1259 OID 4484736)
-- Name: act_idx_hi_act_inst_end; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_act_inst_end ON basicdb_flowable.act_hi_actinst USING btree (end_time_);


--
-- TOC entry 4176 (class 1259 OID 4484746)
-- Name: act_idx_hi_act_inst_exec; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_act_inst_exec ON basicdb_flowable.act_hi_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 4177 (class 1259 OID 4484745)
-- Name: act_idx_hi_act_inst_procinst; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_act_inst_procinst ON basicdb_flowable.act_hi_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 4178 (class 1259 OID 4484735)
-- Name: act_idx_hi_act_inst_start; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_act_inst_start ON basicdb_flowable.act_hi_actinst USING btree (start_time_);


--
-- TOC entry 4181 (class 1259 OID 4484738)
-- Name: act_idx_hi_detail_act_inst; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_detail_act_inst ON basicdb_flowable.act_hi_detail USING btree (act_inst_id_);


--
-- TOC entry 4182 (class 1259 OID 4484740)
-- Name: act_idx_hi_detail_name; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_detail_name ON basicdb_flowable.act_hi_detail USING btree (name_);


--
-- TOC entry 4183 (class 1259 OID 4484737)
-- Name: act_idx_hi_detail_proc_inst; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_detail_proc_inst ON basicdb_flowable.act_hi_detail USING btree (proc_inst_id_);


--
-- TOC entry 4184 (class 1259 OID 4484741)
-- Name: act_idx_hi_detail_task_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_detail_task_id ON basicdb_flowable.act_hi_detail USING btree (task_id_);


--
-- TOC entry 4185 (class 1259 OID 4484739)
-- Name: act_idx_hi_detail_time; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_detail_time ON basicdb_flowable.act_hi_detail USING btree (time_);


--
-- TOC entry 4027 (class 1259 OID 4484209)
-- Name: act_idx_hi_ent_lnk_ref_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_ent_lnk_ref_scope ON basicdb_flowable.act_hi_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 4028 (class 1259 OID 4484210)
-- Name: act_idx_hi_ent_lnk_root_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_ent_lnk_root_scope ON basicdb_flowable.act_hi_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 4029 (class 1259 OID 4484208)
-- Name: act_idx_hi_ent_lnk_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_ent_lnk_scope ON basicdb_flowable.act_hi_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 4030 (class 1259 OID 4484211)
-- Name: act_idx_hi_ent_lnk_scope_def; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_ent_lnk_scope_def ON basicdb_flowable.act_hi_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 4013 (class 1259 OID 4484748)
-- Name: act_idx_hi_ident_lnk_procinst; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_procinst ON basicdb_flowable.act_hi_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 4014 (class 1259 OID 4484187)
-- Name: act_idx_hi_ident_lnk_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_scope ON basicdb_flowable.act_hi_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 4015 (class 1259 OID 4484189)
-- Name: act_idx_hi_ident_lnk_scope_def; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_scope_def ON basicdb_flowable.act_hi_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4016 (class 1259 OID 4484188)
-- Name: act_idx_hi_ident_lnk_sub_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_sub_scope ON basicdb_flowable.act_hi_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4017 (class 1259 OID 4484747)
-- Name: act_idx_hi_ident_lnk_task; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_task ON basicdb_flowable.act_hi_identitylink USING btree (task_id_);


--
-- TOC entry 4018 (class 1259 OID 4484186)
-- Name: act_idx_hi_ident_lnk_user; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_user ON basicdb_flowable.act_hi_identitylink USING btree (user_id_);


--
-- TOC entry 4170 (class 1259 OID 4484733)
-- Name: act_idx_hi_pro_i_buskey; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_pro_i_buskey ON basicdb_flowable.act_hi_procinst USING btree (business_key_);


--
-- TOC entry 4171 (class 1259 OID 4484732)
-- Name: act_idx_hi_pro_inst_end; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_pro_inst_end ON basicdb_flowable.act_hi_procinst USING btree (end_time_);


--
-- TOC entry 4172 (class 1259 OID 4484734)
-- Name: act_idx_hi_pro_super_procinst; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_pro_super_procinst ON basicdb_flowable.act_hi_procinst USING btree (super_process_instance_id_);


--
-- TOC entry 4063 (class 1259 OID 4484744)
-- Name: act_idx_hi_procvar_exe; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_procvar_exe ON basicdb_flowable.act_hi_varinst USING btree (execution_id_);


--
-- TOC entry 4064 (class 1259 OID 4484280)
-- Name: act_idx_hi_procvar_name_type; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_procvar_name_type ON basicdb_flowable.act_hi_varinst USING btree (name_, var_type_);


--
-- TOC entry 4065 (class 1259 OID 4484742)
-- Name: act_idx_hi_procvar_proc_inst; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_procvar_proc_inst ON basicdb_flowable.act_hi_varinst USING btree (proc_inst_id_);


--
-- TOC entry 4066 (class 1259 OID 4484743)
-- Name: act_idx_hi_procvar_task_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_procvar_task_id ON basicdb_flowable.act_hi_varinst USING btree (task_id_);


--
-- TOC entry 4047 (class 1259 OID 4484749)
-- Name: act_idx_hi_task_inst_procinst; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_task_inst_procinst ON basicdb_flowable.act_hi_taskinst USING btree (proc_inst_id_);


--
-- TOC entry 4048 (class 1259 OID 4484254)
-- Name: act_idx_hi_task_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_task_scope ON basicdb_flowable.act_hi_taskinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 4049 (class 1259 OID 4484256)
-- Name: act_idx_hi_task_scope_def; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_task_scope_def ON basicdb_flowable.act_hi_taskinst USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4050 (class 1259 OID 4484255)
-- Name: act_idx_hi_task_sub_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_task_sub_scope ON basicdb_flowable.act_hi_taskinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4067 (class 1259 OID 4484281)
-- Name: act_idx_hi_var_scope_id_type; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_var_scope_id_type ON basicdb_flowable.act_hi_varinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 4068 (class 1259 OID 4484282)
-- Name: act_idx_hi_var_sub_id_type; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_hi_var_sub_id_type ON basicdb_flowable.act_hi_varinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4002 (class 1259 OID 4484175)
-- Name: act_idx_ident_lnk_group; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ident_lnk_group ON basicdb_flowable.act_ru_identitylink USING btree (group_id_);


--
-- TOC entry 4003 (class 1259 OID 4484176)
-- Name: act_idx_ident_lnk_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ident_lnk_scope ON basicdb_flowable.act_ru_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 4004 (class 1259 OID 4484178)
-- Name: act_idx_ident_lnk_scope_def; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ident_lnk_scope_def ON basicdb_flowable.act_ru_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4005 (class 1259 OID 4484177)
-- Name: act_idx_ident_lnk_sub_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ident_lnk_sub_scope ON basicdb_flowable.act_ru_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4006 (class 1259 OID 4484174)
-- Name: act_idx_ident_lnk_user; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ident_lnk_user ON basicdb_flowable.act_ru_identitylink USING btree (user_id_);


--
-- TOC entry 4007 (class 1259 OID 4484546)
-- Name: act_idx_idl_procinst; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_idl_procinst ON basicdb_flowable.act_ru_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 4069 (class 1259 OID 4484333)
-- Name: act_idx_job_correlation_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_job_correlation_id ON basicdb_flowable.act_ru_job USING btree (correlation_id_);


--
-- TOC entry 4070 (class 1259 OID 4484332)
-- Name: act_idx_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_job_custom_values_id ON basicdb_flowable.act_ru_job USING btree (custom_values_id_);


--
-- TOC entry 4071 (class 1259 OID 4484331)
-- Name: act_idx_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_job_exception_stack_id ON basicdb_flowable.act_ru_job USING btree (exception_stack_id_);


--
-- TOC entry 4072 (class 1259 OID 4484582)
-- Name: act_idx_job_execution_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_job_execution_id ON basicdb_flowable.act_ru_job USING btree (execution_id_);


--
-- TOC entry 4073 (class 1259 OID 4484594)
-- Name: act_idx_job_proc_def_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_job_proc_def_id ON basicdb_flowable.act_ru_job USING btree (proc_def_id_);


--
-- TOC entry 4074 (class 1259 OID 4484588)
-- Name: act_idx_job_process_instance_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_job_process_instance_id ON basicdb_flowable.act_ru_job USING btree (process_instance_id_);


--
-- TOC entry 4075 (class 1259 OID 4484397)
-- Name: act_idx_job_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_job_scope ON basicdb_flowable.act_ru_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4076 (class 1259 OID 4484399)
-- Name: act_idx_job_scope_def; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_job_scope_def ON basicdb_flowable.act_ru_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4077 (class 1259 OID 4484398)
-- Name: act_idx_job_sub_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_job_sub_scope ON basicdb_flowable.act_ru_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4131 (class 1259 OID 4484671)
-- Name: act_idx_model_deployment; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_model_deployment ON basicdb_flowable.act_re_model USING btree (deployment_id_);


--
-- TOC entry 4132 (class 1259 OID 4484659)
-- Name: act_idx_model_source; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_model_source ON basicdb_flowable.act_re_model USING btree (editor_source_value_id_);


--
-- TOC entry 4133 (class 1259 OID 4484665)
-- Name: act_idx_model_source_extra; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_model_source_extra ON basicdb_flowable.act_re_model USING btree (editor_source_extra_value_id_);


--
-- TOC entry 4151 (class 1259 OID 4484677)
-- Name: act_idx_procdef_info_json; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_procdef_info_json ON basicdb_flowable.act_procdef_info USING btree (info_json_id_);


--
-- TOC entry 4152 (class 1259 OID 4484683)
-- Name: act_idx_procdef_info_proc; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_procdef_info_proc ON basicdb_flowable.act_procdef_info USING btree (proc_def_id_);


--
-- TOC entry 4157 (class 1259 OID 4484497)
-- Name: act_idx_ru_acti_end; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ru_acti_end ON basicdb_flowable.act_ru_actinst USING btree (end_time_);


--
-- TOC entry 4158 (class 1259 OID 4484500)
-- Name: act_idx_ru_acti_exec; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ru_acti_exec ON basicdb_flowable.act_ru_actinst USING btree (execution_id_);


--
-- TOC entry 4159 (class 1259 OID 4484501)
-- Name: act_idx_ru_acti_exec_act; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ru_acti_exec_act ON basicdb_flowable.act_ru_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 4160 (class 1259 OID 4484498)
-- Name: act_idx_ru_acti_proc; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ru_acti_proc ON basicdb_flowable.act_ru_actinst USING btree (proc_inst_id_);


--
-- TOC entry 4161 (class 1259 OID 4484499)
-- Name: act_idx_ru_acti_proc_act; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ru_acti_proc_act ON basicdb_flowable.act_ru_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 4162 (class 1259 OID 4484496)
-- Name: act_idx_ru_acti_start; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ru_acti_start ON basicdb_flowable.act_ru_actinst USING btree (start_time_);


--
-- TOC entry 4163 (class 1259 OID 4484502)
-- Name: act_idx_ru_acti_task; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ru_acti_task ON basicdb_flowable.act_ru_actinst USING btree (task_id_);


--
-- TOC entry 4053 (class 1259 OID 4484264)
-- Name: act_idx_ru_var_scope_id_type; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ru_var_scope_id_type ON basicdb_flowable.act_ru_variable USING btree (scope_id_, scope_type_);


--
-- TOC entry 4054 (class 1259 OID 4484265)
-- Name: act_idx_ru_var_sub_id_type; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_ru_var_sub_id_type ON basicdb_flowable.act_ru_variable USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4092 (class 1259 OID 4484403)
-- Name: act_idx_sjob_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_sjob_scope ON basicdb_flowable.act_ru_suspended_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4093 (class 1259 OID 4484405)
-- Name: act_idx_sjob_scope_def; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_sjob_scope_def ON basicdb_flowable.act_ru_suspended_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4094 (class 1259 OID 4484404)
-- Name: act_idx_sjob_sub_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_sjob_sub_scope ON basicdb_flowable.act_ru_suspended_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4095 (class 1259 OID 4484340)
-- Name: act_idx_suspended_job_correlation_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_suspended_job_correlation_id ON basicdb_flowable.act_ru_suspended_job USING btree (correlation_id_);


--
-- TOC entry 4096 (class 1259 OID 4484339)
-- Name: act_idx_suspended_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_suspended_job_custom_values_id ON basicdb_flowable.act_ru_suspended_job USING btree (custom_values_id_);


--
-- TOC entry 4097 (class 1259 OID 4484338)
-- Name: act_idx_suspended_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_suspended_job_exception_stack_id ON basicdb_flowable.act_ru_suspended_job USING btree (exception_stack_id_);


--
-- TOC entry 4098 (class 1259 OID 4484618)
-- Name: act_idx_suspended_job_execution_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_suspended_job_execution_id ON basicdb_flowable.act_ru_suspended_job USING btree (execution_id_);


--
-- TOC entry 4099 (class 1259 OID 4484630)
-- Name: act_idx_suspended_job_proc_def_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_suspended_job_proc_def_id ON basicdb_flowable.act_ru_suspended_job USING btree (proc_def_id_);


--
-- TOC entry 4100 (class 1259 OID 4484624)
-- Name: act_idx_suspended_job_process_instance_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_suspended_job_process_instance_id ON basicdb_flowable.act_ru_suspended_job USING btree (process_instance_id_);


--
-- TOC entry 4036 (class 1259 OID 4484231)
-- Name: act_idx_task_create; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_task_create ON basicdb_flowable.act_ru_task USING btree (create_time_);


--
-- TOC entry 4037 (class 1259 OID 4484552)
-- Name: act_idx_task_exec; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_task_exec ON basicdb_flowable.act_ru_task USING btree (execution_id_);


--
-- TOC entry 4038 (class 1259 OID 4484564)
-- Name: act_idx_task_procdef; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_task_procdef ON basicdb_flowable.act_ru_task USING btree (proc_def_id_);


--
-- TOC entry 4039 (class 1259 OID 4484558)
-- Name: act_idx_task_procinst; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_task_procinst ON basicdb_flowable.act_ru_task USING btree (proc_inst_id_);


--
-- TOC entry 4040 (class 1259 OID 4484232)
-- Name: act_idx_task_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_task_scope ON basicdb_flowable.act_ru_task USING btree (scope_id_, scope_type_);


--
-- TOC entry 4041 (class 1259 OID 4484234)
-- Name: act_idx_task_scope_def; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_task_scope_def ON basicdb_flowable.act_ru_task USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4042 (class 1259 OID 4484233)
-- Name: act_idx_task_sub_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_task_sub_scope ON basicdb_flowable.act_ru_task USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4080 (class 1259 OID 4484336)
-- Name: act_idx_timer_job_correlation_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_timer_job_correlation_id ON basicdb_flowable.act_ru_timer_job USING btree (correlation_id_);


--
-- TOC entry 4081 (class 1259 OID 4484335)
-- Name: act_idx_timer_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_timer_job_custom_values_id ON basicdb_flowable.act_ru_timer_job USING btree (custom_values_id_);


--
-- TOC entry 4082 (class 1259 OID 4484337)
-- Name: act_idx_timer_job_duedate; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_timer_job_duedate ON basicdb_flowable.act_ru_timer_job USING btree (duedate_);


--
-- TOC entry 4083 (class 1259 OID 4484334)
-- Name: act_idx_timer_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_timer_job_exception_stack_id ON basicdb_flowable.act_ru_timer_job USING btree (exception_stack_id_);


--
-- TOC entry 4084 (class 1259 OID 4484600)
-- Name: act_idx_timer_job_execution_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_timer_job_execution_id ON basicdb_flowable.act_ru_timer_job USING btree (execution_id_);


--
-- TOC entry 4085 (class 1259 OID 4484612)
-- Name: act_idx_timer_job_proc_def_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_timer_job_proc_def_id ON basicdb_flowable.act_ru_timer_job USING btree (proc_def_id_);


--
-- TOC entry 4086 (class 1259 OID 4484606)
-- Name: act_idx_timer_job_process_instance_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_timer_job_process_instance_id ON basicdb_flowable.act_ru_timer_job USING btree (process_instance_id_);


--
-- TOC entry 4087 (class 1259 OID 4484400)
-- Name: act_idx_tjob_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_tjob_scope ON basicdb_flowable.act_ru_timer_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4088 (class 1259 OID 4484402)
-- Name: act_idx_tjob_scope_def; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_tjob_scope_def ON basicdb_flowable.act_ru_timer_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4089 (class 1259 OID 4484401)
-- Name: act_idx_tjob_sub_scope; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_tjob_sub_scope ON basicdb_flowable.act_ru_timer_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4008 (class 1259 OID 4484534)
-- Name: act_idx_tskass_task; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_tskass_task ON basicdb_flowable.act_ru_identitylink USING btree (task_id_);


--
-- TOC entry 4055 (class 1259 OID 4484266)
-- Name: act_idx_var_bytearray; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_var_bytearray ON basicdb_flowable.act_ru_variable USING btree (bytearray_id_);


--
-- TOC entry 4056 (class 1259 OID 4484570)
-- Name: act_idx_var_exe; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_var_exe ON basicdb_flowable.act_ru_variable USING btree (execution_id_);


--
-- TOC entry 4057 (class 1259 OID 4484576)
-- Name: act_idx_var_procinst; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_var_procinst ON basicdb_flowable.act_ru_variable USING btree (proc_inst_id_);


--
-- TOC entry 4058 (class 1259 OID 4484494)
-- Name: act_idx_variable_task_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX act_idx_variable_task_id ON basicdb_flowable.act_ru_variable USING btree (task_id_);


--
-- TOC entry 3919 (class 1259 OID 4483919)
-- Name: dict_type; Type: INDEX; Schema: basicdb_flowable; Owner: postgres
--

CREATE UNIQUE INDEX dict_type ON basicdb_flowable.sys_dict_type USING btree (dict_type);


--
-- TOC entry 4126 (class 1259 OID 4484428)
-- Name: flw_idx_batch_part; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX flw_idx_batch_part ON basicdb_flowable.flw_ru_batch_part USING btree (batch_id_);


--
-- TOC entry 3920 (class 1259 OID 4484109)
-- Name: idx_sys_files_activity; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_sys_files_activity ON basicdb_flowable.sys_files USING btree (activity_id);


--
-- TOC entry 3921 (class 1259 OID 4484107)
-- Name: idx_sys_files_proc_inst; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_sys_files_proc_inst ON basicdb_flowable.sys_files USING btree (process_instance_id);


--
-- TOC entry 3922 (class 1259 OID 4484108)
-- Name: idx_sys_files_task; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_sys_files_task ON basicdb_flowable.sys_files USING btree (task_id);


--
-- TOC entry 3977 (class 1259 OID 4484090)
-- Name: idx_tbl_flow_extend_procinst_ins_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_tbl_flow_extend_procinst_ins_id ON basicdb_flowable.tbl_flow_extend_procinst USING btree (proc_ins_id);


--
-- TOC entry 3980 (class 1259 OID 4484099)
-- Name: idx_wf_category_org_link_category_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_wf_category_org_link_category_id ON basicdb_flowable.wf_category_org_link USING btree (category_id);


--
-- TOC entry 3981 (class 1259 OID 4484100)
-- Name: idx_wf_category_org_link_org_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_wf_category_org_link_org_id ON basicdb_flowable.wf_category_org_link USING btree (org_id);


--
-- TOC entry 3971 (class 1259 OID 4484050)
-- Name: idx_wf_comment_info_proc_ins_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_wf_comment_info_proc_ins_id ON basicdb_flowable.wf_comment_info USING btree (proc_ins_id);


--
-- TOC entry 3972 (class 1259 OID 4484051)
-- Name: idx_wf_comment_info_task_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_wf_comment_info_task_id ON basicdb_flowable.wf_comment_info USING btree (task_id);


--
-- TOC entry 3967 (class 1259 OID 4484029)
-- Name: idx_wf_copy_instance_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_wf_copy_instance_id ON basicdb_flowable.wf_copy USING btree (instance_id);


--
-- TOC entry 3968 (class 1259 OID 4484028)
-- Name: idx_wf_copy_user_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_wf_copy_user_id ON basicdb_flowable.wf_copy USING btree (user_id);


--
-- TOC entry 3959 (class 1259 OID 4483989)
-- Name: idx_wf_deploy_form_deploy_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_wf_deploy_form_deploy_id ON basicdb_flowable.wf_deploy_form USING btree (deploy_id);


--
-- TOC entry 3960 (class 1259 OID 4483990)
-- Name: idx_wf_deploy_form_form_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_wf_deploy_form_form_id ON basicdb_flowable.wf_deploy_form USING btree (form_id);


--
-- TOC entry 3963 (class 1259 OID 4484007)
-- Name: idx_wf_task_form_proc_ins_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_wf_task_form_proc_ins_id ON basicdb_flowable.wf_task_form USING btree (proc_ins_id);


--
-- TOC entry 3964 (class 1259 OID 4484008)
-- Name: idx_wf_task_form_task_id; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE INDEX idx_wf_task_form_task_id ON basicdb_flowable.wf_task_form USING btree (task_id);


--
-- TOC entry 3908 (class 1259 OID 4483920)
-- Name: sched_name; Type: INDEX; Schema: basicdb_flowable; Owner: postgres
--

CREATE INDEX sched_name ON basicdb_flowable.qrtz_triggers USING btree (sched_name, job_name, job_group);


--
-- TOC entry 4203 (class 1259 OID 4487133)
-- Name: uk_sys_notice_read_notice_user; Type: INDEX; Schema: basicdb_flowable; Owner: system
--

CREATE UNIQUE INDEX uk_sys_notice_read_notice_user ON basicdb_flowable.sys_notice_read USING btree (notice_id, user_id);


--
-- TOC entry 4205 (class 2606 OID 4484541)
-- Name: act_ru_identitylink act_fk_athrz_procedef; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_identitylink
    ADD CONSTRAINT act_fk_athrz_procedef FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable.act_re_procdef(id_);


--
-- TOC entry 4204 (class 2606 OID 4484503)
-- Name: act_ge_bytearray act_fk_bytearr_depl; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ge_bytearray
    ADD CONSTRAINT act_fk_bytearr_depl FOREIGN KEY (deployment_id_) REFERENCES basicdb_flowable.act_re_deployment(id_);


--
-- TOC entry 4230 (class 2606 OID 4484382)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4231 (class 2606 OID 4484377)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4232 (class 2606 OID 4484637)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_execution; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_execution FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4233 (class 2606 OID 4484649)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_proc_def; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable.act_re_procdef(id_);


--
-- TOC entry 4234 (class 2606 OID 4484643)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_process_instance; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4208 (class 2606 OID 4484654)
-- Name: act_ru_event_subscr act_fk_event_exec; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_event_subscr
    ADD CONSTRAINT act_fk_event_exec FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4241 (class 2606 OID 4484517)
-- Name: act_ru_execution act_fk_exe_parent; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_execution
    ADD CONSTRAINT act_fk_exe_parent FOREIGN KEY (parent_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4242 (class 2606 OID 4484529)
-- Name: act_ru_execution act_fk_exe_procdef; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procdef FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable.act_re_procdef(id_);


--
-- TOC entry 4243 (class 2606 OID 4484511)
-- Name: act_ru_execution act_fk_exe_procinst; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procinst FOREIGN KEY (proc_inst_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4244 (class 2606 OID 4484523)
-- Name: act_ru_execution act_fk_exe_super; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_execution
    ADD CONSTRAINT act_fk_exe_super FOREIGN KEY (super_exec_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4235 (class 2606 OID 4484392)
-- Name: act_ru_external_job act_fk_external_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4236 (class 2606 OID 4484387)
-- Name: act_ru_external_job act_fk_external_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4206 (class 2606 OID 4484547)
-- Name: act_ru_identitylink act_fk_idl_procinst; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_identitylink
    ADD CONSTRAINT act_fk_idl_procinst FOREIGN KEY (proc_inst_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4245 (class 2606 OID 4484678)
-- Name: act_procdef_info act_fk_info_json_ba; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_procdef_info
    ADD CONSTRAINT act_fk_info_json_ba FOREIGN KEY (info_json_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4246 (class 2606 OID 4484684)
-- Name: act_procdef_info act_fk_info_procdef; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_procdef_info
    ADD CONSTRAINT act_fk_info_procdef FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable.act_re_procdef(id_);


--
-- TOC entry 4215 (class 2606 OID 4484352)
-- Name: act_ru_job act_fk_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_job
    ADD CONSTRAINT act_fk_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4216 (class 2606 OID 4484347)
-- Name: act_ru_job act_fk_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_job
    ADD CONSTRAINT act_fk_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4217 (class 2606 OID 4484583)
-- Name: act_ru_job act_fk_job_execution; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_job
    ADD CONSTRAINT act_fk_job_execution FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4218 (class 2606 OID 4484595)
-- Name: act_ru_job act_fk_job_proc_def; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_job
    ADD CONSTRAINT act_fk_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable.act_re_procdef(id_);


--
-- TOC entry 4219 (class 2606 OID 4484589)
-- Name: act_ru_job act_fk_job_process_instance; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_job
    ADD CONSTRAINT act_fk_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4238 (class 2606 OID 4484672)
-- Name: act_re_model act_fk_model_deployment; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_re_model
    ADD CONSTRAINT act_fk_model_deployment FOREIGN KEY (deployment_id_) REFERENCES basicdb_flowable.act_re_deployment(id_);


--
-- TOC entry 4239 (class 2606 OID 4484660)
-- Name: act_re_model act_fk_model_source; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_re_model
    ADD CONSTRAINT act_fk_model_source FOREIGN KEY (editor_source_value_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4240 (class 2606 OID 4484666)
-- Name: act_re_model act_fk_model_source_extra; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_re_model
    ADD CONSTRAINT act_fk_model_source_extra FOREIGN KEY (editor_source_extra_value_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4225 (class 2606 OID 4484372)
-- Name: act_ru_suspended_job act_fk_suspended_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4226 (class 2606 OID 4484367)
-- Name: act_ru_suspended_job act_fk_suspended_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4227 (class 2606 OID 4484619)
-- Name: act_ru_suspended_job act_fk_suspended_job_execution; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_execution FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4228 (class 2606 OID 4484631)
-- Name: act_ru_suspended_job act_fk_suspended_job_proc_def; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable.act_re_procdef(id_);


--
-- TOC entry 4229 (class 2606 OID 4484625)
-- Name: act_ru_suspended_job act_fk_suspended_job_process_instance; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4209 (class 2606 OID 4484553)
-- Name: act_ru_task act_fk_task_exe; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_task
    ADD CONSTRAINT act_fk_task_exe FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4210 (class 2606 OID 4484565)
-- Name: act_ru_task act_fk_task_procdef; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_task
    ADD CONSTRAINT act_fk_task_procdef FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable.act_re_procdef(id_);


--
-- TOC entry 4211 (class 2606 OID 4484559)
-- Name: act_ru_task act_fk_task_procinst; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_task
    ADD CONSTRAINT act_fk_task_procinst FOREIGN KEY (proc_inst_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4220 (class 2606 OID 4484362)
-- Name: act_ru_timer_job act_fk_timer_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4221 (class 2606 OID 4484357)
-- Name: act_ru_timer_job act_fk_timer_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4222 (class 2606 OID 4484601)
-- Name: act_ru_timer_job act_fk_timer_job_execution; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_execution FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4223 (class 2606 OID 4484613)
-- Name: act_ru_timer_job act_fk_timer_job_proc_def; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable.act_re_procdef(id_);


--
-- TOC entry 4224 (class 2606 OID 4484607)
-- Name: act_ru_timer_job act_fk_timer_job_process_instance; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4207 (class 2606 OID 4484535)
-- Name: act_ru_identitylink act_fk_tskass_task; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_identitylink
    ADD CONSTRAINT act_fk_tskass_task FOREIGN KEY (task_id_) REFERENCES basicdb_flowable.act_ru_task(id_);


--
-- TOC entry 4212 (class 2606 OID 4484267)
-- Name: act_ru_variable act_fk_var_bytearray; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_variable
    ADD CONSTRAINT act_fk_var_bytearray FOREIGN KEY (bytearray_id_) REFERENCES basicdb_flowable.act_ge_bytearray(id_);


--
-- TOC entry 4213 (class 2606 OID 4484571)
-- Name: act_ru_variable act_fk_var_exe; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_variable
    ADD CONSTRAINT act_fk_var_exe FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4214 (class 2606 OID 4484577)
-- Name: act_ru_variable act_fk_var_procinst; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.act_ru_variable
    ADD CONSTRAINT act_fk_var_procinst FOREIGN KEY (proc_inst_id_) REFERENCES basicdb_flowable.act_ru_execution(id_);


--
-- TOC entry 4237 (class 2606 OID 4484429)
-- Name: flw_ru_batch_part flw_fk_batch_part_parent; Type: FK CONSTRAINT; Schema: basicdb_flowable; Owner: system
--

ALTER TABLE ONLY basicdb_flowable.flw_ru_batch_part
    ADD CONSTRAINT flw_fk_batch_part_parent FOREIGN KEY (batch_id_) REFERENCES basicdb_flowable.flw_ru_batch(id_);


--
-- TOC entry 4515 (class 0 OID 0)
-- Dependencies: 268
-- Name: TABLE gen_table; Type: ACL; Schema: basicdb_flowable; Owner: system
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.gen_table TO PUBLIC;


--
-- TOC entry 4516 (class 0 OID 0)
-- Dependencies: 269
-- Name: TABLE gen_table_column; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.gen_table_column TO PUBLIC;


--
-- TOC entry 4518 (class 0 OID 0)
-- Dependencies: 270
-- Name: SEQUENCE gen_table_column_column_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.gen_table_column_column_id_seq TO PUBLIC;


--
-- TOC entry 4520 (class 0 OID 0)
-- Dependencies: 271
-- Name: SEQUENCE gen_table_table_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: system
--

GRANT ALL ON SEQUENCE basicdb_flowable.gen_table_table_id_seq TO PUBLIC;


--
-- TOC entry 4521 (class 0 OID 0)
-- Dependencies: 272
-- Name: TABLE list_column; Type: ACL; Schema: basicdb_flowable; Owner: system
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.list_column TO PUBLIC;


--
-- TOC entry 4522 (class 0 OID 0)
-- Dependencies: 273
-- Name: TABLE list_table; Type: ACL; Schema: basicdb_flowable; Owner: system
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.list_table TO PUBLIC;


--
-- TOC entry 4523 (class 0 OID 0)
-- Dependencies: 274
-- Name: TABLE qrtz_blob_triggers; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.qrtz_blob_triggers TO PUBLIC;


--
-- TOC entry 4524 (class 0 OID 0)
-- Dependencies: 275
-- Name: TABLE qrtz_calendars; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.qrtz_calendars TO PUBLIC;


--
-- TOC entry 4525 (class 0 OID 0)
-- Dependencies: 276
-- Name: TABLE qrtz_cron_triggers; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.qrtz_cron_triggers TO PUBLIC;


--
-- TOC entry 4526 (class 0 OID 0)
-- Dependencies: 277
-- Name: TABLE qrtz_fired_triggers; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.qrtz_fired_triggers TO PUBLIC;


--
-- TOC entry 4527 (class 0 OID 0)
-- Dependencies: 278
-- Name: TABLE qrtz_job_details; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.qrtz_job_details TO PUBLIC;


--
-- TOC entry 4528 (class 0 OID 0)
-- Dependencies: 279
-- Name: TABLE qrtz_locks; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.qrtz_locks TO PUBLIC;


--
-- TOC entry 4529 (class 0 OID 0)
-- Dependencies: 280
-- Name: TABLE qrtz_paused_trigger_grps; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.qrtz_paused_trigger_grps TO PUBLIC;


--
-- TOC entry 4530 (class 0 OID 0)
-- Dependencies: 281
-- Name: TABLE qrtz_scheduler_state; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.qrtz_scheduler_state TO PUBLIC;


--
-- TOC entry 4531 (class 0 OID 0)
-- Dependencies: 282
-- Name: TABLE qrtz_simple_triggers; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.qrtz_simple_triggers TO PUBLIC;


--
-- TOC entry 4532 (class 0 OID 0)
-- Dependencies: 283
-- Name: TABLE qrtz_simprop_triggers; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.qrtz_simprop_triggers TO PUBLIC;


--
-- TOC entry 4533 (class 0 OID 0)
-- Dependencies: 284
-- Name: TABLE qrtz_triggers; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.qrtz_triggers TO PUBLIC;


--
-- TOC entry 4534 (class 0 OID 0)
-- Dependencies: 285
-- Name: TABLE sys_config; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_config TO PUBLIC;


--
-- TOC entry 4536 (class 0 OID 0)
-- Dependencies: 286
-- Name: SEQUENCE sys_config_config_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_config_config_id_seq TO PUBLIC;


--
-- TOC entry 4537 (class 0 OID 0)
-- Dependencies: 288
-- Name: TABLE sys_dept; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_dept TO PUBLIC;


--
-- TOC entry 4539 (class 0 OID 0)
-- Dependencies: 289
-- Name: SEQUENCE sys_dept_dept_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_dept_dept_id_seq TO PUBLIC;


--
-- TOC entry 4540 (class 0 OID 0)
-- Dependencies: 290
-- Name: TABLE sys_dict_data; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_dict_data TO PUBLIC;


--
-- TOC entry 4542 (class 0 OID 0)
-- Dependencies: 291
-- Name: SEQUENCE sys_dict_data_dict_code_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_dict_data_dict_code_seq TO PUBLIC;


--
-- TOC entry 4543 (class 0 OID 0)
-- Dependencies: 292
-- Name: TABLE sys_dict_type; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_dict_type TO PUBLIC;


--
-- TOC entry 4545 (class 0 OID 0)
-- Dependencies: 293
-- Name: SEQUENCE sys_dict_type_dict_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_dict_type_dict_id_seq TO PUBLIC;


--
-- TOC entry 4556 (class 0 OID 0)
-- Dependencies: 296
-- Name: TABLE sys_job; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_job TO PUBLIC;


--
-- TOC entry 4558 (class 0 OID 0)
-- Dependencies: 297
-- Name: SEQUENCE sys_job_job_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_job_job_id_seq TO PUBLIC;


--
-- TOC entry 4559 (class 0 OID 0)
-- Dependencies: 298
-- Name: TABLE sys_job_log; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_job_log TO PUBLIC;


--
-- TOC entry 4561 (class 0 OID 0)
-- Dependencies: 299
-- Name: SEQUENCE sys_job_log_job_log_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_job_log_job_log_id_seq TO PUBLIC;


--
-- TOC entry 4562 (class 0 OID 0)
-- Dependencies: 300
-- Name: TABLE sys_logininfor; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_logininfor TO PUBLIC;


--
-- TOC entry 4564 (class 0 OID 0)
-- Dependencies: 301
-- Name: SEQUENCE sys_logininfor_info_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_logininfor_info_id_seq TO PUBLIC;


--
-- TOC entry 4566 (class 0 OID 0)
-- Dependencies: 302
-- Name: TABLE sys_menu; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_menu TO PUBLIC;


--
-- TOC entry 4568 (class 0 OID 0)
-- Dependencies: 303
-- Name: SEQUENCE sys_menu_menu_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_menu_menu_id_seq TO PUBLIC;


--
-- TOC entry 4569 (class 0 OID 0)
-- Dependencies: 304
-- Name: TABLE sys_notice; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_notice TO PUBLIC;


--
-- TOC entry 4571 (class 0 OID 0)
-- Dependencies: 305
-- Name: SEQUENCE sys_notice_notice_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_notice_notice_id_seq TO PUBLIC;


--
-- TOC entry 4573 (class 0 OID 0)
-- Dependencies: 306
-- Name: TABLE sys_oper_log; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_oper_log TO PUBLIC;


--
-- TOC entry 4575 (class 0 OID 0)
-- Dependencies: 307
-- Name: SEQUENCE sys_oper_log_oper_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_oper_log_oper_id_seq TO PUBLIC;


--
-- TOC entry 4576 (class 0 OID 0)
-- Dependencies: 308
-- Name: TABLE sys_post; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_post TO PUBLIC;


--
-- TOC entry 4578 (class 0 OID 0)
-- Dependencies: 309
-- Name: SEQUENCE sys_post_post_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_post_post_id_seq TO PUBLIC;


--
-- TOC entry 4581 (class 0 OID 0)
-- Dependencies: 310
-- Name: TABLE sys_role; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_role TO PUBLIC;


--
-- TOC entry 4582 (class 0 OID 0)
-- Dependencies: 311
-- Name: TABLE sys_role_dept; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_role_dept TO PUBLIC;


--
-- TOC entry 4583 (class 0 OID 0)
-- Dependencies: 312
-- Name: TABLE sys_role_menu; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_role_menu TO PUBLIC;


--
-- TOC entry 4585 (class 0 OID 0)
-- Dependencies: 313
-- Name: SEQUENCE sys_role_role_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_role_role_id_seq TO PUBLIC;


--
-- TOC entry 4586 (class 0 OID 0)
-- Dependencies: 314
-- Name: TABLE sys_user; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_user TO PUBLIC;


--
-- TOC entry 4587 (class 0 OID 0)
-- Dependencies: 315
-- Name: TABLE sys_user_post; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_user_post TO PUBLIC;


--
-- TOC entry 4588 (class 0 OID 0)
-- Dependencies: 316
-- Name: TABLE sys_user_role; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable.sys_user_role TO PUBLIC;


--
-- TOC entry 4590 (class 0 OID 0)
-- Dependencies: 317
-- Name: SEQUENCE sys_user_user_id_seq; Type: ACL; Schema: basicdb_flowable; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable.sys_user_user_id_seq TO PUBLIC;


-- Completed on 2026-10-16 09:50:44

--
-- PostgreSQL database dump complete
--

