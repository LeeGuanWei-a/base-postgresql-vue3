--
-- PostgreSQL database dump
--

-- Dumped from database version 15.1
-- Dumped by pg_dump version 17.0

-- Started on 2026-10-23 17:16:26

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

ALTER TABLE ONLY basicdb_flowable7.flw_event_resource DROP CONSTRAINT flw_fk_event_rsrc_dpl;
ALTER TABLE ONLY basicdb_flowable7.flw_ru_batch_part DROP CONSTRAINT flw_fk_batch_part_parent;
ALTER TABLE ONLY basicdb_flowable7.act_ru_variable DROP CONSTRAINT act_fk_var_procinst;
ALTER TABLE ONLY basicdb_flowable7.act_ru_variable DROP CONSTRAINT act_fk_var_exe;
ALTER TABLE ONLY basicdb_flowable7.act_ru_variable DROP CONSTRAINT act_fk_var_bytearray;
ALTER TABLE ONLY basicdb_flowable7.act_ru_identitylink DROP CONSTRAINT act_fk_tskass_task;
ALTER TABLE ONLY basicdb_flowable7.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_process_instance;
ALTER TABLE ONLY basicdb_flowable7.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_proc_def;
ALTER TABLE ONLY basicdb_flowable7.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_execution;
ALTER TABLE ONLY basicdb_flowable7.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_exception;
ALTER TABLE ONLY basicdb_flowable7.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_custom_values;
ALTER TABLE ONLY basicdb_flowable7.act_ru_task DROP CONSTRAINT act_fk_task_procinst;
ALTER TABLE ONLY basicdb_flowable7.act_ru_task DROP CONSTRAINT act_fk_task_procdef;
ALTER TABLE ONLY basicdb_flowable7.act_ru_task DROP CONSTRAINT act_fk_task_exe;
ALTER TABLE ONLY basicdb_flowable7.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_process_instance;
ALTER TABLE ONLY basicdb_flowable7.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_proc_def;
ALTER TABLE ONLY basicdb_flowable7.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_execution;
ALTER TABLE ONLY basicdb_flowable7.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_exception;
ALTER TABLE ONLY basicdb_flowable7.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_custom_values;
ALTER TABLE ONLY basicdb_flowable7.act_re_model DROP CONSTRAINT act_fk_model_source_extra;
ALTER TABLE ONLY basicdb_flowable7.act_re_model DROP CONSTRAINT act_fk_model_source;
ALTER TABLE ONLY basicdb_flowable7.act_re_model DROP CONSTRAINT act_fk_model_deployment;
ALTER TABLE ONLY basicdb_flowable7.act_ru_job DROP CONSTRAINT act_fk_job_process_instance;
ALTER TABLE ONLY basicdb_flowable7.act_ru_job DROP CONSTRAINT act_fk_job_proc_def;
ALTER TABLE ONLY basicdb_flowable7.act_ru_job DROP CONSTRAINT act_fk_job_execution;
ALTER TABLE ONLY basicdb_flowable7.act_ru_job DROP CONSTRAINT act_fk_job_exception;
ALTER TABLE ONLY basicdb_flowable7.act_ru_job DROP CONSTRAINT act_fk_job_custom_values;
ALTER TABLE ONLY basicdb_flowable7.act_procdef_info DROP CONSTRAINT act_fk_info_procdef;
ALTER TABLE ONLY basicdb_flowable7.act_procdef_info DROP CONSTRAINT act_fk_info_json_ba;
ALTER TABLE ONLY basicdb_flowable7.act_ru_identitylink DROP CONSTRAINT act_fk_idl_procinst;
ALTER TABLE ONLY basicdb_flowable7.act_ru_external_job DROP CONSTRAINT act_fk_external_job_exception;
ALTER TABLE ONLY basicdb_flowable7.act_ru_external_job DROP CONSTRAINT act_fk_external_job_custom_values;
ALTER TABLE ONLY basicdb_flowable7.act_ru_execution DROP CONSTRAINT act_fk_exe_super;
ALTER TABLE ONLY basicdb_flowable7.act_ru_execution DROP CONSTRAINT act_fk_exe_procinst;
ALTER TABLE ONLY basicdb_flowable7.act_ru_execution DROP CONSTRAINT act_fk_exe_procdef;
ALTER TABLE ONLY basicdb_flowable7.act_ru_execution DROP CONSTRAINT act_fk_exe_parent;
ALTER TABLE ONLY basicdb_flowable7.act_ru_event_subscr DROP CONSTRAINT act_fk_event_exec;
ALTER TABLE ONLY basicdb_flowable7.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_process_instance;
ALTER TABLE ONLY basicdb_flowable7.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_proc_def;
ALTER TABLE ONLY basicdb_flowable7.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_execution;
ALTER TABLE ONLY basicdb_flowable7.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_exception;
ALTER TABLE ONLY basicdb_flowable7.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_custom_values;
ALTER TABLE ONLY basicdb_flowable7.act_ge_bytearray DROP CONSTRAINT act_fk_bytearr_depl;
ALTER TABLE ONLY basicdb_flowable7.act_ru_identitylink DROP CONSTRAINT act_fk_athrz_procedef;
DROP INDEX basicdb_flowable7.uk_sys_notice_read_notice_user;
DROP INDEX basicdb_flowable7.sched_name;
DROP INDEX basicdb_flowable7.idx_wf_task_form_task_id;
DROP INDEX basicdb_flowable7.idx_wf_task_form_proc_ins_id;
DROP INDEX basicdb_flowable7.idx_wf_task_form_form_id;
DROP INDEX basicdb_flowable7.idx_wf_form_code;
DROP INDEX basicdb_flowable7.idx_wf_form_category_parent;
DROP INDEX basicdb_flowable7.idx_wf_form_category_id;
DROP INDEX basicdb_flowable7.idx_wf_deploy_form_form_id;
DROP INDEX basicdb_flowable7.idx_wf_deploy_form_deploy_id;
DROP INDEX basicdb_flowable7.idx_wf_copy_user_id;
DROP INDEX basicdb_flowable7.idx_wf_copy_instance_id;
DROP INDEX basicdb_flowable7.idx_wf_comment_info_task_id;
DROP INDEX basicdb_flowable7.idx_wf_comment_info_proc_ins_id;
DROP INDEX basicdb_flowable7.idx_wf_category_parent;
DROP INDEX basicdb_flowable7.idx_wf_category_code;
DROP INDEX basicdb_flowable7.idx_extend_procinst_proc_ins_id;
DROP INDEX basicdb_flowable7.idx_extend_procinst_model_key;
DROP INDEX basicdb_flowable7.flw_idx_event_rsrc_dpl;
DROP INDEX basicdb_flowable7.flw_idx_batch_part;
DROP INDEX basicdb_flowable7.dict_type;
DROP INDEX basicdb_flowable7.act_idx_variable_task_id;
DROP INDEX basicdb_flowable7.act_idx_var_procinst;
DROP INDEX basicdb_flowable7.act_idx_var_exe;
DROP INDEX basicdb_flowable7.act_idx_var_bytearray;
DROP INDEX basicdb_flowable7.act_idx_tskass_task;
DROP INDEX basicdb_flowable7.act_idx_tjob_sub_scope;
DROP INDEX basicdb_flowable7.act_idx_tjob_scope_def;
DROP INDEX basicdb_flowable7.act_idx_tjob_scope;
DROP INDEX basicdb_flowable7.act_idx_timer_job_process_instance_id;
DROP INDEX basicdb_flowable7.act_idx_timer_job_proc_def_id;
DROP INDEX basicdb_flowable7.act_idx_timer_job_execution_id;
DROP INDEX basicdb_flowable7.act_idx_timer_job_exception_stack_id;
DROP INDEX basicdb_flowable7.act_idx_timer_job_duedate;
DROP INDEX basicdb_flowable7.act_idx_timer_job_custom_values_id;
DROP INDEX basicdb_flowable7.act_idx_timer_job_correlation_id;
DROP INDEX basicdb_flowable7.act_idx_task_sub_scope;
DROP INDEX basicdb_flowable7.act_idx_task_scope_def;
DROP INDEX basicdb_flowable7.act_idx_task_scope;
DROP INDEX basicdb_flowable7.act_idx_task_procinst;
DROP INDEX basicdb_flowable7.act_idx_task_procdef;
DROP INDEX basicdb_flowable7.act_idx_task_exec;
DROP INDEX basicdb_flowable7.act_idx_task_create;
DROP INDEX basicdb_flowable7.act_idx_suspended_job_process_instance_id;
DROP INDEX basicdb_flowable7.act_idx_suspended_job_proc_def_id;
DROP INDEX basicdb_flowable7.act_idx_suspended_job_execution_id;
DROP INDEX basicdb_flowable7.act_idx_suspended_job_exception_stack_id;
DROP INDEX basicdb_flowable7.act_idx_suspended_job_custom_values_id;
DROP INDEX basicdb_flowable7.act_idx_suspended_job_correlation_id;
DROP INDEX basicdb_flowable7.act_idx_sjob_sub_scope;
DROP INDEX basicdb_flowable7.act_idx_sjob_scope_def;
DROP INDEX basicdb_flowable7.act_idx_sjob_scope;
DROP INDEX basicdb_flowable7.act_idx_ru_var_sub_id_type;
DROP INDEX basicdb_flowable7.act_idx_ru_var_scope_id_type;
DROP INDEX basicdb_flowable7.act_idx_ru_acti_task;
DROP INDEX basicdb_flowable7.act_idx_ru_acti_start;
DROP INDEX basicdb_flowable7.act_idx_ru_acti_proc_act;
DROP INDEX basicdb_flowable7.act_idx_ru_acti_proc;
DROP INDEX basicdb_flowable7.act_idx_ru_acti_exec_act;
DROP INDEX basicdb_flowable7.act_idx_ru_acti_exec;
DROP INDEX basicdb_flowable7.act_idx_ru_acti_end;
DROP INDEX basicdb_flowable7.act_idx_procdef_info_proc;
DROP INDEX basicdb_flowable7.act_idx_procdef_info_json;
DROP INDEX basicdb_flowable7.act_idx_model_source_extra;
DROP INDEX basicdb_flowable7.act_idx_model_source;
DROP INDEX basicdb_flowable7.act_idx_model_deployment;
DROP INDEX basicdb_flowable7.act_idx_job_sub_scope;
DROP INDEX basicdb_flowable7.act_idx_job_scope_def;
DROP INDEX basicdb_flowable7.act_idx_job_scope;
DROP INDEX basicdb_flowable7.act_idx_job_process_instance_id;
DROP INDEX basicdb_flowable7.act_idx_job_proc_def_id;
DROP INDEX basicdb_flowable7.act_idx_job_execution_id;
DROP INDEX basicdb_flowable7.act_idx_job_exception_stack_id;
DROP INDEX basicdb_flowable7.act_idx_job_custom_values_id;
DROP INDEX basicdb_flowable7.act_idx_job_correlation_id;
DROP INDEX basicdb_flowable7.act_idx_idl_procinst;
DROP INDEX basicdb_flowable7.act_idx_ident_lnk_user;
DROP INDEX basicdb_flowable7.act_idx_ident_lnk_sub_scope;
DROP INDEX basicdb_flowable7.act_idx_ident_lnk_scope_def;
DROP INDEX basicdb_flowable7.act_idx_ident_lnk_scope;
DROP INDEX basicdb_flowable7.act_idx_ident_lnk_group;
DROP INDEX basicdb_flowable7.act_idx_hi_var_sub_id_type;
DROP INDEX basicdb_flowable7.act_idx_hi_var_scope_id_type;
DROP INDEX basicdb_flowable7.act_idx_hi_task_sub_scope;
DROP INDEX basicdb_flowable7.act_idx_hi_task_scope_def;
DROP INDEX basicdb_flowable7.act_idx_hi_task_scope;
DROP INDEX basicdb_flowable7.act_idx_hi_task_inst_procinst;
DROP INDEX basicdb_flowable7.act_idx_hi_procvar_task_id;
DROP INDEX basicdb_flowable7.act_idx_hi_procvar_proc_inst;
DROP INDEX basicdb_flowable7.act_idx_hi_procvar_name_type;
DROP INDEX basicdb_flowable7.act_idx_hi_procvar_exe;
DROP INDEX basicdb_flowable7.act_idx_hi_pro_super_procinst;
DROP INDEX basicdb_flowable7.act_idx_hi_pro_inst_end;
DROP INDEX basicdb_flowable7.act_idx_hi_pro_i_buskey;
DROP INDEX basicdb_flowable7.act_idx_hi_ident_lnk_user;
DROP INDEX basicdb_flowable7.act_idx_hi_ident_lnk_task;
DROP INDEX basicdb_flowable7.act_idx_hi_ident_lnk_sub_scope;
DROP INDEX basicdb_flowable7.act_idx_hi_ident_lnk_scope_def;
DROP INDEX basicdb_flowable7.act_idx_hi_ident_lnk_scope;
DROP INDEX basicdb_flowable7.act_idx_hi_ident_lnk_procinst;
DROP INDEX basicdb_flowable7.act_idx_hi_ent_lnk_scope_def;
DROP INDEX basicdb_flowable7.act_idx_hi_ent_lnk_scope;
DROP INDEX basicdb_flowable7.act_idx_hi_ent_lnk_root_scope;
DROP INDEX basicdb_flowable7.act_idx_hi_ent_lnk_ref_scope;
DROP INDEX basicdb_flowable7.act_idx_hi_detail_time;
DROP INDEX basicdb_flowable7.act_idx_hi_detail_task_id;
DROP INDEX basicdb_flowable7.act_idx_hi_detail_proc_inst;
DROP INDEX basicdb_flowable7.act_idx_hi_detail_name;
DROP INDEX basicdb_flowable7.act_idx_hi_detail_act_inst;
DROP INDEX basicdb_flowable7.act_idx_hi_act_inst_start;
DROP INDEX basicdb_flowable7.act_idx_hi_act_inst_procinst;
DROP INDEX basicdb_flowable7.act_idx_hi_act_inst_exec;
DROP INDEX basicdb_flowable7.act_idx_hi_act_inst_end;
DROP INDEX basicdb_flowable7.act_idx_external_job_exception_stack_id;
DROP INDEX basicdb_flowable7.act_idx_external_job_custom_values_id;
DROP INDEX basicdb_flowable7.act_idx_external_job_correlation_id;
DROP INDEX basicdb_flowable7.act_idx_exec_ref_id_;
DROP INDEX basicdb_flowable7.act_idx_exec_buskey;
DROP INDEX basicdb_flowable7.act_idx_exe_super;
DROP INDEX basicdb_flowable7.act_idx_exe_root;
DROP INDEX basicdb_flowable7.act_idx_exe_procinst;
DROP INDEX basicdb_flowable7.act_idx_exe_procdef;
DROP INDEX basicdb_flowable7.act_idx_exe_parent;
DROP INDEX basicdb_flowable7.act_idx_event_subscr_scoperef_;
DROP INDEX basicdb_flowable7.act_idx_event_subscr_proc_id;
DROP INDEX basicdb_flowable7.act_idx_event_subscr_config_;
DROP INDEX basicdb_flowable7.act_idx_event_subscr;
DROP INDEX basicdb_flowable7.act_idx_event_def_uniq;
DROP INDEX basicdb_flowable7.act_idx_ent_lnk_scope_def;
DROP INDEX basicdb_flowable7.act_idx_ent_lnk_scope;
DROP INDEX basicdb_flowable7.act_idx_ent_lnk_root_scope;
DROP INDEX basicdb_flowable7.act_idx_ent_lnk_ref_scope;
DROP INDEX basicdb_flowable7.act_idx_ejob_sub_scope;
DROP INDEX basicdb_flowable7.act_idx_ejob_scope_def;
DROP INDEX basicdb_flowable7.act_idx_ejob_scope;
DROP INDEX basicdb_flowable7.act_idx_djob_sub_scope;
DROP INDEX basicdb_flowable7.act_idx_djob_scope_def;
DROP INDEX basicdb_flowable7.act_idx_djob_scope;
DROP INDEX basicdb_flowable7.act_idx_deadletter_job_process_instance_id;
DROP INDEX basicdb_flowable7.act_idx_deadletter_job_proc_def_id;
DROP INDEX basicdb_flowable7.act_idx_deadletter_job_execution_id;
DROP INDEX basicdb_flowable7.act_idx_deadletter_job_exception_stack_id;
DROP INDEX basicdb_flowable7.act_idx_deadletter_job_custom_values_id;
DROP INDEX basicdb_flowable7.act_idx_deadletter_job_correlation_id;
DROP INDEX basicdb_flowable7.act_idx_channel_def_uniq;
DROP INDEX basicdb_flowable7.act_idx_bytear_depl;
DROP INDEX basicdb_flowable7.act_idx_athrz_procedef;
DROP INDEX basicdb_flowable7.act_idx_act_hi_tsk_log_task;
ALTER TABLE ONLY basicdb_flowable7.wf_task_form DROP CONSTRAINT wf_task_form_pkey;
ALTER TABLE ONLY basicdb_flowable7.wf_form_template DROP CONSTRAINT wf_form_template_pkey;
ALTER TABLE ONLY basicdb_flowable7.wf_form DROP CONSTRAINT wf_form_pkey;
ALTER TABLE ONLY basicdb_flowable7.wf_form_category DROP CONSTRAINT wf_form_category_pkey;
ALTER TABLE ONLY basicdb_flowable7.wf_deploy_form DROP CONSTRAINT wf_deploy_form_pkey;
ALTER TABLE ONLY basicdb_flowable7.wf_copy DROP CONSTRAINT wf_copy_pkey;
ALTER TABLE ONLY basicdb_flowable7.wf_comment_info DROP CONSTRAINT wf_comment_info_pkey;
ALTER TABLE ONLY basicdb_flowable7.wf_category DROP CONSTRAINT wf_category_pkey;
ALTER TABLE ONLY basicdb_flowable7.tbl_flow_extend_procinst DROP CONSTRAINT tbl_flow_extend_procinst_pkey;
ALTER TABLE ONLY basicdb_flowable7.sys_notice_read DROP CONSTRAINT sys_notice_read_pkey;
ALTER TABLE ONLY basicdb_flowable7.sys_files DROP CONSTRAINT sys_files_pkey;
ALTER TABLE ONLY basicdb_flowable7.sys_demo DROP CONSTRAINT sys_demo_pkey;
ALTER TABLE ONLY basicdb_flowable7.flw_ru_batch DROP CONSTRAINT flw_ru_batch_pkey;
ALTER TABLE ONLY basicdb_flowable7.flw_ru_batch_part DROP CONSTRAINT flw_ru_batch_part_pkey;
ALTER TABLE ONLY basicdb_flowable7.flw_expression DROP CONSTRAINT flw_expression_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_re_procdef DROP CONSTRAINT act_uniq_procdef;
ALTER TABLE ONLY basicdb_flowable7.act_procdef_info DROP CONSTRAINT act_uniq_info_procdef;
ALTER TABLE ONLY basicdb_flowable7.act_ru_variable DROP CONSTRAINT act_ru_variable_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ru_timer_job DROP CONSTRAINT act_ru_timer_job_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ru_task DROP CONSTRAINT act_ru_task_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ru_suspended_job DROP CONSTRAINT act_ru_suspended_job_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ru_job DROP CONSTRAINT act_ru_job_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ru_identitylink DROP CONSTRAINT act_ru_identitylink_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ru_history_job DROP CONSTRAINT act_ru_history_job_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ru_external_job DROP CONSTRAINT act_ru_external_job_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ru_execution DROP CONSTRAINT act_ru_execution_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ru_event_subscr DROP CONSTRAINT act_ru_event_subscr_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ru_entitylink DROP CONSTRAINT act_ru_entitylink_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ru_deadletter_job DROP CONSTRAINT act_ru_deadletter_job_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ru_actinst DROP CONSTRAINT act_ru_actinst_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_re_procdef DROP CONSTRAINT act_re_procdef_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_re_model DROP CONSTRAINT act_re_model_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_re_deployment DROP CONSTRAINT act_re_deployment_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_procdef_info DROP CONSTRAINT act_procdef_info_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_hi_varinst DROP CONSTRAINT act_hi_varinst_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_hi_tsk_log DROP CONSTRAINT act_hi_tsk_log_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_hi_taskinst DROP CONSTRAINT act_hi_taskinst_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_hi_procinst DROP CONSTRAINT act_hi_procinst_proc_inst_id__key;
ALTER TABLE ONLY basicdb_flowable7.act_hi_procinst DROP CONSTRAINT act_hi_procinst_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_hi_identitylink DROP CONSTRAINT act_hi_identitylink_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_hi_entitylink DROP CONSTRAINT act_hi_entitylink_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_hi_detail DROP CONSTRAINT act_hi_detail_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_hi_comment DROP CONSTRAINT act_hi_comment_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_hi_attachment DROP CONSTRAINT act_hi_attachment_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_hi_actinst DROP CONSTRAINT act_hi_actinst_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ge_property DROP CONSTRAINT act_ge_property_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_ge_bytearray DROP CONSTRAINT act_ge_bytearray_pkey;
ALTER TABLE ONLY basicdb_flowable7.act_evt_log DROP CONSTRAINT act_evt_log_pkey;
ALTER TABLE ONLY basicdb_flowable7.sys_user_role DROP CONSTRAINT "PRIMARY00028";
ALTER TABLE ONLY basicdb_flowable7.sys_user_post DROP CONSTRAINT "PRIMARY00027";
ALTER TABLE ONLY basicdb_flowable7.sys_user DROP CONSTRAINT "PRIMARY00026";
ALTER TABLE ONLY basicdb_flowable7.sys_role_menu DROP CONSTRAINT "PRIMARY00025";
ALTER TABLE ONLY basicdb_flowable7.sys_role_dept DROP CONSTRAINT "PRIMARY00024";
ALTER TABLE ONLY basicdb_flowable7.sys_role DROP CONSTRAINT "PRIMARY00023";
ALTER TABLE ONLY basicdb_flowable7.sys_post DROP CONSTRAINT "PRIMARY00022";
ALTER TABLE ONLY basicdb_flowable7.sys_oper_log DROP CONSTRAINT "PRIMARY00021";
ALTER TABLE ONLY basicdb_flowable7.sys_notice DROP CONSTRAINT "PRIMARY00020";
ALTER TABLE ONLY basicdb_flowable7.sys_menu DROP CONSTRAINT "PRIMARY00019";
ALTER TABLE ONLY basicdb_flowable7.sys_logininfor DROP CONSTRAINT "PRIMARY00018";
ALTER TABLE ONLY basicdb_flowable7.sys_job_log DROP CONSTRAINT "PRIMARY00017";
ALTER TABLE ONLY basicdb_flowable7.sys_job DROP CONSTRAINT "PRIMARY00016";
ALTER TABLE ONLY basicdb_flowable7.sys_dict_type DROP CONSTRAINT "PRIMARY00015";
ALTER TABLE ONLY basicdb_flowable7.sys_dict_data DROP CONSTRAINT "PRIMARY00014";
ALTER TABLE ONLY basicdb_flowable7.sys_dept DROP CONSTRAINT "PRIMARY00013";
ALTER TABLE ONLY basicdb_flowable7.sys_config DROP CONSTRAINT "PRIMARY00012";
ALTER TABLE ONLY basicdb_flowable7.qrtz_triggers DROP CONSTRAINT "PRIMARY00011";
ALTER TABLE ONLY basicdb_flowable7.qrtz_simprop_triggers DROP CONSTRAINT "PRIMARY00010";
ALTER TABLE ONLY basicdb_flowable7.qrtz_simple_triggers DROP CONSTRAINT "PRIMARY00009";
ALTER TABLE ONLY basicdb_flowable7.qrtz_scheduler_state DROP CONSTRAINT "PRIMARY00008";
ALTER TABLE ONLY basicdb_flowable7.qrtz_paused_trigger_grps DROP CONSTRAINT "PRIMARY00007";
ALTER TABLE ONLY basicdb_flowable7.qrtz_locks DROP CONSTRAINT "PRIMARY00006";
ALTER TABLE ONLY basicdb_flowable7.qrtz_job_details DROP CONSTRAINT "PRIMARY00005";
ALTER TABLE ONLY basicdb_flowable7.qrtz_fired_triggers DROP CONSTRAINT "PRIMARY00004";
ALTER TABLE ONLY basicdb_flowable7.qrtz_cron_triggers DROP CONSTRAINT "PRIMARY00003";
ALTER TABLE ONLY basicdb_flowable7.qrtz_calendars DROP CONSTRAINT "PRIMARY00002";
ALTER TABLE ONLY basicdb_flowable7.qrtz_blob_triggers DROP CONSTRAINT "PRIMARY00001";
ALTER TABLE ONLY basicdb_flowable7.gen_table_column DROP CONSTRAINT "PRIMARY00000";
ALTER TABLE ONLY basicdb_flowable7.gen_table DROP CONSTRAINT "PRIMARY";
ALTER TABLE ONLY basicdb_flowable7.flw_event_resource DROP CONSTRAINT "FLW_EVENT_RESOURCE_pkey";
ALTER TABLE ONLY basicdb_flowable7.flw_event_deployment DROP CONSTRAINT "FLW_EVENT_DEPLOYMENT_pkey";
ALTER TABLE ONLY basicdb_flowable7.flw_event_definition DROP CONSTRAINT "FLW_EVENT_DEFINITION_pkey";
ALTER TABLE ONLY basicdb_flowable7.flw_channel_definition DROP CONSTRAINT "FLW_CHANNEL_DEFINITION_pkey";
ALTER TABLE basicdb_flowable7.sys_user ALTER COLUMN user_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.sys_role ALTER COLUMN role_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.sys_post ALTER COLUMN post_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.sys_oper_log ALTER COLUMN oper_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.sys_notice ALTER COLUMN notice_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.sys_menu ALTER COLUMN menu_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.sys_logininfor ALTER COLUMN info_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.sys_job_log ALTER COLUMN job_log_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.sys_job ALTER COLUMN job_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.sys_dict_type ALTER COLUMN dict_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.sys_dict_data ALTER COLUMN dict_code DROP DEFAULT;
ALTER TABLE basicdb_flowable7.sys_dept ALTER COLUMN dept_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.sys_config ALTER COLUMN config_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.gen_table_column ALTER COLUMN column_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.gen_table ALTER COLUMN table_id DROP DEFAULT;
ALTER TABLE basicdb_flowable7.act_hi_tsk_log ALTER COLUMN id_ DROP DEFAULT;
ALTER TABLE basicdb_flowable7.act_evt_log ALTER COLUMN log_nr_ DROP DEFAULT;
DROP TABLE basicdb_flowable7.wf_task_form;
DROP TABLE basicdb_flowable7.wf_form_template;
DROP TABLE basicdb_flowable7.wf_form_category;
DROP TABLE basicdb_flowable7.wf_form;
DROP TABLE basicdb_flowable7.wf_deploy_form;
DROP TABLE basicdb_flowable7.wf_copy;
DROP TABLE basicdb_flowable7.wf_comment_info;
DROP TABLE basicdb_flowable7.wf_category;
DROP TABLE basicdb_flowable7.tbl_flow_extend_procinst;
DROP SEQUENCE basicdb_flowable7.sys_user_user_id_seq;
DROP TABLE basicdb_flowable7.sys_user_role;
DROP TABLE basicdb_flowable7.sys_user_post;
DROP TABLE basicdb_flowable7.sys_user;
DROP SEQUENCE basicdb_flowable7.sys_role_role_id_seq;
DROP TABLE basicdb_flowable7.sys_role_menu;
DROP TABLE basicdb_flowable7.sys_role_dept;
DROP TABLE basicdb_flowable7.sys_role;
DROP SEQUENCE basicdb_flowable7.sys_post_post_id_seq;
DROP TABLE basicdb_flowable7.sys_post;
DROP SEQUENCE basicdb_flowable7.sys_oper_log_oper_id_seq;
DROP TABLE basicdb_flowable7.sys_oper_log;
DROP SEQUENCE basicdb_flowable7.sys_notice_read_read_id_seq;
DROP TABLE basicdb_flowable7.sys_notice_read;
DROP SEQUENCE basicdb_flowable7.sys_notice_notice_id_seq;
DROP TABLE basicdb_flowable7.sys_notice;
DROP SEQUENCE basicdb_flowable7.sys_menu_menu_id_seq;
DROP TABLE basicdb_flowable7.sys_menu;
DROP SEQUENCE basicdb_flowable7.sys_logininfor_info_id_seq;
DROP TABLE basicdb_flowable7.sys_logininfor;
DROP SEQUENCE basicdb_flowable7.sys_job_log_job_log_id_seq;
DROP TABLE basicdb_flowable7.sys_job_log;
DROP SEQUENCE basicdb_flowable7.sys_job_job_id_seq;
DROP TABLE basicdb_flowable7.sys_job;
DROP TABLE basicdb_flowable7.sys_i18n;
DROP TABLE basicdb_flowable7.sys_files;
DROP SEQUENCE basicdb_flowable7.sys_dict_type_dict_id_seq;
DROP TABLE basicdb_flowable7.sys_dict_type;
DROP SEQUENCE basicdb_flowable7.sys_dict_data_dict_code_seq;
DROP TABLE basicdb_flowable7.sys_dict_data;
DROP SEQUENCE basicdb_flowable7.sys_dept_dept_id_seq;
DROP TABLE basicdb_flowable7.sys_dept;
DROP TABLE basicdb_flowable7.sys_demo;
DROP SEQUENCE basicdb_flowable7.sys_config_config_id_seq;
DROP TABLE basicdb_flowable7.sys_config;
DROP TABLE basicdb_flowable7.qrtz_triggers;
DROP TABLE basicdb_flowable7.qrtz_simprop_triggers;
DROP TABLE basicdb_flowable7.qrtz_simple_triggers;
DROP TABLE basicdb_flowable7.qrtz_scheduler_state;
DROP TABLE basicdb_flowable7.qrtz_paused_trigger_grps;
DROP TABLE basicdb_flowable7.qrtz_locks;
DROP TABLE basicdb_flowable7.qrtz_job_details;
DROP TABLE basicdb_flowable7.qrtz_fired_triggers;
DROP TABLE basicdb_flowable7.qrtz_cron_triggers;
DROP TABLE basicdb_flowable7.qrtz_calendars;
DROP TABLE basicdb_flowable7.qrtz_blob_triggers;
DROP VIEW basicdb_flowable7.list_table;
DROP VIEW basicdb_flowable7.list_column;
DROP SEQUENCE basicdb_flowable7.gen_table_table_id_seq;
DROP SEQUENCE basicdb_flowable7.gen_table_column_column_id_seq;
DROP TABLE basicdb_flowable7.gen_table_column;
DROP TABLE basicdb_flowable7.gen_table;
DROP TABLE basicdb_flowable7.flw_ru_batch_part;
DROP TABLE basicdb_flowable7.flw_ru_batch;
DROP TABLE basicdb_flowable7.flw_expression;
DROP TABLE basicdb_flowable7.flw_event_resource;
DROP TABLE basicdb_flowable7.flw_event_deployment;
DROP TABLE basicdb_flowable7.flw_event_definition;
DROP TABLE basicdb_flowable7.flw_channel_definition;
DROP TABLE basicdb_flowable7.act_ru_variable;
DROP TABLE basicdb_flowable7.act_ru_timer_job;
DROP TABLE basicdb_flowable7.act_ru_task;
DROP TABLE basicdb_flowable7.act_ru_suspended_job;
DROP TABLE basicdb_flowable7.act_ru_job;
DROP TABLE basicdb_flowable7.act_ru_identitylink;
DROP TABLE basicdb_flowable7.act_ru_history_job;
DROP TABLE basicdb_flowable7.act_ru_external_job;
DROP TABLE basicdb_flowable7.act_ru_execution;
DROP TABLE basicdb_flowable7.act_ru_event_subscr;
DROP TABLE basicdb_flowable7.act_ru_entitylink;
DROP TABLE basicdb_flowable7.act_ru_deadletter_job;
DROP TABLE basicdb_flowable7.act_ru_actinst;
DROP TABLE basicdb_flowable7.act_re_procdef;
DROP TABLE basicdb_flowable7.act_re_model;
DROP TABLE basicdb_flowable7.act_re_deployment;
DROP TABLE basicdb_flowable7.act_procdef_info;
DROP TABLE basicdb_flowable7.act_hi_varinst;
DROP SEQUENCE basicdb_flowable7.act_hi_tsk_log_id__seq;
DROP TABLE basicdb_flowable7.act_hi_tsk_log;
DROP TABLE basicdb_flowable7.act_hi_taskinst;
DROP TABLE basicdb_flowable7.act_hi_procinst;
DROP TABLE basicdb_flowable7.act_hi_identitylink;
DROP TABLE basicdb_flowable7.act_hi_entitylink;
DROP TABLE basicdb_flowable7.act_hi_detail;
DROP TABLE basicdb_flowable7.act_hi_comment;
DROP TABLE basicdb_flowable7.act_hi_attachment;
DROP TABLE basicdb_flowable7.act_hi_actinst;
DROP TABLE basicdb_flowable7.act_ge_property;
DROP TABLE basicdb_flowable7.act_ge_bytearray;
DROP SEQUENCE basicdb_flowable7.act_evt_log_log_nr__seq;
DROP TABLE basicdb_flowable7.act_evt_log;
DROP FUNCTION basicdb_flowable7.tabledef(oid);
DROP FUNCTION basicdb_flowable7.ifnull(param1 text, param2 text);
DROP FUNCTION basicdb_flowable7.find_in_set(bigint, character varying);
DROP FUNCTION basicdb_flowable7.exec(sqlstring character varying);
DROP FUNCTION basicdb_flowable7.date_format(indate anyelement, informate text);
DROP SCHEMA basicdb_flowable7;
--
-- TOC entry 11 (class 2615 OID 4556339)
-- Name: basicdb_flowable7; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA basicdb_flowable7;


ALTER SCHEMA basicdb_flowable7 OWNER TO postgres;

--
-- TOC entry 615 (class 1255 OID 4556351)
-- Name: date_format(anyelement, text); Type: FUNCTION; Schema: basicdb_flowable7; Owner: system
--

CREATE FUNCTION basicdb_flowable7.date_format(indate anyelement, informate text) RETURNS text
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


ALTER FUNCTION basicdb_flowable7.date_format(indate anyelement, informate text) OWNER TO system;

--
-- TOC entry 616 (class 1255 OID 4556352)
-- Name: exec(character varying); Type: FUNCTION; Schema: basicdb_flowable7; Owner: system
--

CREATE FUNCTION basicdb_flowable7.exec(sqlstring character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
declare
res varchar(50);
BEGIN
EXECUTE sqlstring;
RETURN 'ok';
END
$$;


ALTER FUNCTION basicdb_flowable7.exec(sqlstring character varying) OWNER TO system;

--
-- TOC entry 617 (class 1255 OID 4556353)
-- Name: find_in_set(bigint, character varying); Type: FUNCTION; Schema: basicdb_flowable7; Owner: system
--

CREATE FUNCTION basicdb_flowable7.find_in_set(bigint, character varying) RETURNS boolean
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


ALTER FUNCTION basicdb_flowable7.find_in_set(bigint, character varying) OWNER TO system;

--
-- TOC entry 588 (class 1255 OID 4556354)
-- Name: ifnull(text, text); Type: FUNCTION; Schema: basicdb_flowable7; Owner: system
--

CREATE FUNCTION basicdb_flowable7.ifnull(param1 text, param2 text) RETURNS text
    LANGUAGE plpgsql
    AS $_$
BEGIN
    return COALESCE($1,$2);
END;
$_$;


ALTER FUNCTION basicdb_flowable7.ifnull(param1 text, param2 text) OWNER TO system;

--
-- TOC entry 618 (class 1255 OID 4556355)
-- Name: tabledef(oid); Type: FUNCTION; Schema: basicdb_flowable7; Owner: system
--

CREATE FUNCTION basicdb_flowable7.tabledef(oid) RETURNS text
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


ALTER FUNCTION basicdb_flowable7.tabledef(oid) OWNER TO system;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 566 (class 1259 OID 4557062)
-- Name: act_evt_log; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_evt_log (
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


ALTER TABLE basicdb_flowable7.act_evt_log OWNER TO system;

--
-- TOC entry 565 (class 1259 OID 4557061)
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: system
--

CREATE SEQUENCE basicdb_flowable7.act_evt_log_log_nr__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.act_evt_log_log_nr__seq OWNER TO system;

--
-- TOC entry 4962 (class 0 OID 0)
-- Dependencies: 565
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: system
--

ALTER SEQUENCE basicdb_flowable7.act_evt_log_log_nr__seq OWNED BY basicdb_flowable7.act_evt_log.log_nr_;


--
-- TOC entry 541 (class 1259 OID 4556752)
-- Name: act_ge_bytearray; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ge_bytearray (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    deployment_id_ character varying(64),
    bytes_ bytea,
    generated_ boolean
);


ALTER TABLE basicdb_flowable7.act_ge_bytearray OWNER TO system;

--
-- TOC entry 540 (class 1259 OID 4556747)
-- Name: act_ge_property; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ge_property (
    name_ character varying(64) NOT NULL,
    value_ character varying(300),
    rev_ integer
);


ALTER TABLE basicdb_flowable7.act_ge_property OWNER TO system;

--
-- TOC entry 570 (class 1259 OID 4557296)
-- Name: act_hi_actinst; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_hi_actinst (
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
    completed_by_ character varying(255),
    start_time_ timestamp without time zone NOT NULL,
    end_time_ timestamp without time zone,
    transaction_order_ integer,
    duration_ bigint,
    delete_reason_ character varying(4000),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable7.act_hi_actinst OWNER TO system;

--
-- TOC entry 573 (class 1259 OID 4557319)
-- Name: act_hi_attachment; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_hi_attachment (
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


ALTER TABLE basicdb_flowable7.act_hi_attachment OWNER TO system;

--
-- TOC entry 572 (class 1259 OID 4557312)
-- Name: act_hi_comment; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_hi_comment (
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


ALTER TABLE basicdb_flowable7.act_hi_comment OWNER TO system;

--
-- TOC entry 571 (class 1259 OID 4557305)
-- Name: act_hi_detail; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_hi_detail (
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


ALTER TABLE basicdb_flowable7.act_hi_detail OWNER TO system;

--
-- TOC entry 543 (class 1259 OID 4556770)
-- Name: act_hi_entitylink; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_hi_entitylink (
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


ALTER TABLE basicdb_flowable7.act_hi_entitylink OWNER TO system;

--
-- TOC entry 545 (class 1259 OID 4556793)
-- Name: act_hi_identitylink; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_hi_identitylink (
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


ALTER TABLE basicdb_flowable7.act_hi_identitylink OWNER TO system;

--
-- TOC entry 569 (class 1259 OID 4557285)
-- Name: act_hi_procinst; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_hi_procinst (
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


ALTER TABLE basicdb_flowable7.act_hi_procinst OWNER TO system;

--
-- TOC entry 555 (class 1259 OID 4556967)
-- Name: act_hi_taskinst; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_hi_taskinst (
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
    state_ character varying(255),
    name_ character varying(255),
    parent_task_id_ character varying(64),
    description_ character varying(4000),
    owner_ character varying(255),
    assignee_ character varying(255),
    start_time_ timestamp without time zone NOT NULL,
    in_progress_time_ timestamp without time zone,
    in_progress_started_by_ character varying(255),
    claim_time_ timestamp without time zone,
    claimed_by_ character varying(255),
    suspended_time_ timestamp without time zone,
    suspended_by_ character varying(255),
    end_time_ timestamp without time zone,
    completed_by_ character varying(255),
    duration_ bigint,
    delete_reason_ character varying(4000),
    priority_ integer,
    in_progress_due_date_ timestamp without time zone,
    due_date_ timestamp without time zone,
    form_key_ character varying(255),
    category_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    last_updated_time_ timestamp without time zone
);


ALTER TABLE basicdb_flowable7.act_hi_taskinst OWNER TO system;

--
-- TOC entry 557 (class 1259 OID 4556977)
-- Name: act_hi_tsk_log; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_hi_tsk_log (
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


ALTER TABLE basicdb_flowable7.act_hi_tsk_log OWNER TO system;

--
-- TOC entry 556 (class 1259 OID 4556976)
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: system
--

CREATE SEQUENCE basicdb_flowable7.act_hi_tsk_log_id__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.act_hi_tsk_log_id__seq OWNER TO system;

--
-- TOC entry 4963 (class 0 OID 0)
-- Dependencies: 556
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: system
--

ALTER SEQUENCE basicdb_flowable7.act_hi_tsk_log_id__seq OWNED BY basicdb_flowable7.act_hi_tsk_log.id_;


--
-- TOC entry 559 (class 1259 OID 4557005)
-- Name: act_hi_varinst; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_hi_varinst (
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
    meta_info_ character varying(4000),
    create_time_ timestamp without time zone,
    last_updated_time_ timestamp without time zone
);


ALTER TABLE basicdb_flowable7.act_hi_varinst OWNER TO system;

--
-- TOC entry 567 (class 1259 OID 4557071)
-- Name: act_procdef_info; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_procdef_info (
    id_ character varying(64) NOT NULL,
    proc_def_id_ character varying(64) NOT NULL,
    rev_ integer,
    info_json_id_ character varying(64)
);


ALTER TABLE basicdb_flowable7.act_procdef_info OWNER TO system;

--
-- TOC entry 561 (class 1259 OID 4557028)
-- Name: act_re_deployment; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_re_deployment (
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


ALTER TABLE basicdb_flowable7.act_re_deployment OWNER TO system;

--
-- TOC entry 562 (class 1259 OID 4557036)
-- Name: act_re_model; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_re_model (
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


ALTER TABLE basicdb_flowable7.act_re_model OWNER TO system;

--
-- TOC entry 564 (class 1259 OID 4557052)
-- Name: act_re_procdef; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_re_procdef (
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


ALTER TABLE basicdb_flowable7.act_re_procdef OWNER TO system;

--
-- TOC entry 568 (class 1259 OID 4557076)
-- Name: act_ru_actinst; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_actinst (
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
    completed_by_ character varying(255),
    start_time_ timestamp without time zone NOT NULL,
    end_time_ timestamp without time zone,
    duration_ bigint,
    transaction_order_ integer,
    delete_reason_ character varying(4000),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable7.act_ru_actinst OWNER TO system;

--
-- TOC entry 549 (class 1259 OID 4556828)
-- Name: act_ru_deadletter_job; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_deadletter_job (
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


ALTER TABLE basicdb_flowable7.act_ru_deadletter_job OWNER TO system;

--
-- TOC entry 542 (class 1259 OID 4556759)
-- Name: act_ru_entitylink; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_entitylink (
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


ALTER TABLE basicdb_flowable7.act_ru_entitylink OWNER TO system;

--
-- TOC entry 560 (class 1259 OID 4557016)
-- Name: act_ru_event_subscr; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_event_subscr (
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
    scope_definition_key_ character varying(255),
    scope_type_ character varying(64),
    lock_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable7.act_ru_event_subscr OWNER TO system;

--
-- TOC entry 563 (class 1259 OID 4557044)
-- Name: act_ru_execution; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_execution (
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


ALTER TABLE basicdb_flowable7.act_ru_execution OWNER TO system;

--
-- TOC entry 551 (class 1259 OID 4556844)
-- Name: act_ru_external_job; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_external_job (
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


ALTER TABLE basicdb_flowable7.act_ru_external_job OWNER TO system;

--
-- TOC entry 550 (class 1259 OID 4556836)
-- Name: act_ru_history_job; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_history_job (
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


ALTER TABLE basicdb_flowable7.act_ru_history_job OWNER TO system;

--
-- TOC entry 544 (class 1259 OID 4556781)
-- Name: act_ru_identitylink; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_identitylink (
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


ALTER TABLE basicdb_flowable7.act_ru_identitylink OWNER TO system;

--
-- TOC entry 546 (class 1259 OID 4556804)
-- Name: act_ru_job; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_job (
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


ALTER TABLE basicdb_flowable7.act_ru_job OWNER TO system;

--
-- TOC entry 548 (class 1259 OID 4556820)
-- Name: act_ru_suspended_job; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_suspended_job (
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


ALTER TABLE basicdb_flowable7.act_ru_suspended_job OWNER TO system;

--
-- TOC entry 554 (class 1259 OID 4556955)
-- Name: act_ru_task; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_task (
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
    state_ character varying(255),
    name_ character varying(255),
    parent_task_id_ character varying(64),
    description_ character varying(4000),
    task_def_key_ character varying(255),
    owner_ character varying(255),
    assignee_ character varying(255),
    delegation_ character varying(64),
    priority_ integer,
    create_time_ timestamp without time zone,
    in_progress_time_ timestamp without time zone,
    in_progress_started_by_ character varying(255),
    claim_time_ timestamp without time zone,
    claimed_by_ character varying(255),
    suspended_time_ timestamp without time zone,
    suspended_by_ character varying(255),
    in_progress_due_date_ timestamp without time zone,
    due_date_ timestamp without time zone,
    category_ character varying(255),
    suspension_state_ integer,
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    form_key_ character varying(255),
    is_count_enabled_ boolean,
    var_count_ integer,
    id_link_count_ integer,
    sub_task_count_ integer
);


ALTER TABLE basicdb_flowable7.act_ru_task OWNER TO system;

--
-- TOC entry 547 (class 1259 OID 4556812)
-- Name: act_ru_timer_job; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_timer_job (
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


ALTER TABLE basicdb_flowable7.act_ru_timer_job OWNER TO system;

--
-- TOC entry 558 (class 1259 OID 4556990)
-- Name: act_ru_variable; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.act_ru_variable (
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
    text2_ character varying(4000),
    meta_info_ character varying(4000)
);


ALTER TABLE basicdb_flowable7.act_ru_variable OWNER TO system;

--
-- TOC entry 577 (class 1259 OID 4557372)
-- Name: flw_channel_definition; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.flw_channel_definition (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    version_ integer,
    key_ character varying(255),
    category_ character varying(255),
    type_ character varying(255),
    implementation_ character varying(255),
    deployment_id_ character varying(255),
    create_time_ timestamp(3) without time zone,
    tenant_id_ character varying(255),
    resource_name_ character varying(255),
    description_ character varying(255)
);


ALTER TABLE basicdb_flowable7.flw_channel_definition OWNER TO system;

--
-- TOC entry 576 (class 1259 OID 4557358)
-- Name: flw_event_definition; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.flw_event_definition (
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


ALTER TABLE basicdb_flowable7.flw_event_definition OWNER TO system;

--
-- TOC entry 574 (class 1259 OID 4557344)
-- Name: flw_event_deployment; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.flw_event_deployment (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    category_ character varying(255),
    deploy_time_ timestamp(3) without time zone,
    tenant_id_ character varying(255),
    parent_deployment_id_ character varying(255)
);


ALTER TABLE basicdb_flowable7.flw_event_deployment OWNER TO system;

--
-- TOC entry 575 (class 1259 OID 4557351)
-- Name: flw_event_resource; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.flw_event_resource (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    deployment_id_ character varying(255),
    resource_bytes_ bytea
);


ALTER TABLE basicdb_flowable7.flw_event_resource OWNER TO system;

--
-- TOC entry 579 (class 1259 OID 4557487)
-- Name: flw_expression; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.flw_expression (
    id bigint NOT NULL,
    name character varying(128) NOT NULL,
    expression text,
    type character varying(32),
    status integer DEFAULT 0,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable7.flw_expression OWNER TO system;

--
-- TOC entry 4964 (class 0 OID 0)
-- Dependencies: 579
-- Name: TABLE flw_expression; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON TABLE basicdb_flowable7.flw_expression IS '流程表达式';


--
-- TOC entry 4965 (class 0 OID 0)
-- Dependencies: 579
-- Name: COLUMN flw_expression.id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.flw_expression.id IS '表达式主键';


--
-- TOC entry 4966 (class 0 OID 0)
-- Dependencies: 579
-- Name: COLUMN flw_expression.name; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.flw_expression.name IS '表达式名称';


--
-- TOC entry 4967 (class 0 OID 0)
-- Dependencies: 579
-- Name: COLUMN flw_expression.expression; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.flw_expression.expression IS '表达式内容';


--
-- TOC entry 4968 (class 0 OID 0)
-- Dependencies: 579
-- Name: COLUMN flw_expression.type; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.flw_expression.type IS '表达式类型';


--
-- TOC entry 4969 (class 0 OID 0)
-- Dependencies: 579
-- Name: COLUMN flw_expression.status; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.flw_expression.status IS '启用状态';


--
-- TOC entry 552 (class 1259 OID 4556933)
-- Name: flw_ru_batch; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.flw_ru_batch (
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


ALTER TABLE basicdb_flowable7.flw_ru_batch OWNER TO system;

--
-- TOC entry 553 (class 1259 OID 4556941)
-- Name: flw_ru_batch_part; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.flw_ru_batch_part (
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


ALTER TABLE basicdb_flowable7.flw_ru_batch_part OWNER TO system;

--
-- TOC entry 488 (class 1259 OID 4556356)
-- Name: gen_table; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.gen_table (
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


ALTER TABLE basicdb_flowable7.gen_table OWNER TO system;

--
-- TOC entry 489 (class 1259 OID 4556364)
-- Name: gen_table_column; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.gen_table_column (
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


ALTER TABLE basicdb_flowable7.gen_table_column OWNER TO postgres;

--
-- TOC entry 490 (class 1259 OID 4556370)
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.gen_table_column_column_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.gen_table_column_column_id_seq OWNER TO postgres;

--
-- TOC entry 4972 (class 0 OID 0)
-- Dependencies: 490
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.gen_table_column_column_id_seq OWNED BY basicdb_flowable7.gen_table_column.column_id;


--
-- TOC entry 491 (class 1259 OID 4556371)
-- Name: gen_table_table_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: system
--

CREATE SEQUENCE basicdb_flowable7.gen_table_table_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.gen_table_table_id_seq OWNER TO system;

--
-- TOC entry 4974 (class 0 OID 0)
-- Dependencies: 491
-- Name: gen_table_table_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: system
--

ALTER SEQUENCE basicdb_flowable7.gen_table_table_id_seq OWNED BY basicdb_flowable7.gen_table.table_id;


--
-- TOC entry 492 (class 1259 OID 4556372)
-- Name: list_column; Type: VIEW; Schema: basicdb_flowable7; Owner: system
--

CREATE VIEW basicdb_flowable7.list_column AS
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
  WHERE ((c.relkind = ANY (ARRAY['r'::"char", 'p'::"char"])) AND (a.attnum > 0) AND (n.nspname = 'basicdb_flowable7'::name))
  ORDER BY c.relname, a.attnum;


ALTER VIEW basicdb_flowable7.list_column OWNER TO system;

--
-- TOC entry 493 (class 1259 OID 4556377)
-- Name: list_table; Type: VIEW; Schema: basicdb_flowable7; Owner: system
--

CREATE VIEW basicdb_flowable7.list_table AS
 SELECT c.relname AS table_name,
    obj_description(c.oid) AS table_comment,
    CURRENT_TIMESTAMP AS create_time,
    CURRENT_TIMESTAMP AS update_time
   FROM (pg_class c
     LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace)))
  WHERE ((c.relkind = ANY (ARRAY['r'::"char", 'p'::"char"])) AND (c.relname !~~ 'spatial_%'::text) AND (n.nspname = 'basicdb_flowable7'::name) AND (n.nspname <> ''::name));


ALTER VIEW basicdb_flowable7.list_table OWNER TO system;

--
-- TOC entry 494 (class 1259 OID 4556382)
-- Name: qrtz_blob_triggers; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.qrtz_blob_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea
);


ALTER TABLE basicdb_flowable7.qrtz_blob_triggers OWNER TO postgres;

--
-- TOC entry 495 (class 1259 OID 4556387)
-- Name: qrtz_calendars; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.qrtz_calendars (
    sched_name character varying(120) NOT NULL,
    calendar_name character varying(200) NOT NULL,
    calendar bytea NOT NULL
);


ALTER TABLE basicdb_flowable7.qrtz_calendars OWNER TO postgres;

--
-- TOC entry 496 (class 1259 OID 4556392)
-- Name: qrtz_cron_triggers; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.qrtz_cron_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(200) NOT NULL,
    time_zone_id character varying(80)
);


ALTER TABLE basicdb_flowable7.qrtz_cron_triggers OWNER TO postgres;

--
-- TOC entry 497 (class 1259 OID 4556397)
-- Name: qrtz_fired_triggers; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.qrtz_fired_triggers (
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


ALTER TABLE basicdb_flowable7.qrtz_fired_triggers OWNER TO postgres;

--
-- TOC entry 498 (class 1259 OID 4556402)
-- Name: qrtz_job_details; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.qrtz_job_details (
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


ALTER TABLE basicdb_flowable7.qrtz_job_details OWNER TO postgres;

--
-- TOC entry 499 (class 1259 OID 4556407)
-- Name: qrtz_locks; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.qrtz_locks (
    sched_name character varying(120) NOT NULL,
    lock_name character varying(40) NOT NULL
);


ALTER TABLE basicdb_flowable7.qrtz_locks OWNER TO postgres;

--
-- TOC entry 500 (class 1259 OID 4556410)
-- Name: qrtz_paused_trigger_grps; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.qrtz_paused_trigger_grps (
    sched_name character varying(120) NOT NULL,
    trigger_group character varying(200) NOT NULL
);


ALTER TABLE basicdb_flowable7.qrtz_paused_trigger_grps OWNER TO postgres;

--
-- TOC entry 501 (class 1259 OID 4556413)
-- Name: qrtz_scheduler_state; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.qrtz_scheduler_state (
    sched_name character varying(120) NOT NULL,
    instance_name character varying(200) NOT NULL,
    last_checkin_time bigint NOT NULL,
    checkin_interval bigint NOT NULL
);


ALTER TABLE basicdb_flowable7.qrtz_scheduler_state OWNER TO postgres;

--
-- TOC entry 502 (class 1259 OID 4556416)
-- Name: qrtz_simple_triggers; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.qrtz_simple_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count bigint NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL
);


ALTER TABLE basicdb_flowable7.qrtz_simple_triggers OWNER TO postgres;

--
-- TOC entry 503 (class 1259 OID 4556421)
-- Name: qrtz_simprop_triggers; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.qrtz_simprop_triggers (
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


ALTER TABLE basicdb_flowable7.qrtz_simprop_triggers OWNER TO postgres;

--
-- TOC entry 504 (class 1259 OID 4556426)
-- Name: qrtz_triggers; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.qrtz_triggers (
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


ALTER TABLE basicdb_flowable7.qrtz_triggers OWNER TO postgres;

--
-- TOC entry 505 (class 1259 OID 4556431)
-- Name: sys_config; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_config (
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


ALTER TABLE basicdb_flowable7.sys_config OWNER TO postgres;

--
-- TOC entry 506 (class 1259 OID 4556437)
-- Name: sys_config_config_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_config_config_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_config_config_id_seq OWNER TO postgres;

--
-- TOC entry 4990 (class 0 OID 0)
-- Dependencies: 506
-- Name: sys_config_config_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_config_config_id_seq OWNED BY basicdb_flowable7.sys_config.config_id;


--
-- TOC entry 507 (class 1259 OID 4556438)
-- Name: sys_demo; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.sys_demo (
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


ALTER TABLE basicdb_flowable7.sys_demo OWNER TO system;

--
-- TOC entry 508 (class 1259 OID 4556441)
-- Name: sys_dept; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_dept (
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


ALTER TABLE basicdb_flowable7.sys_dept OWNER TO postgres;

--
-- TOC entry 509 (class 1259 OID 4556450)
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_dept_dept_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_dept_dept_id_seq OWNER TO postgres;

--
-- TOC entry 4993 (class 0 OID 0)
-- Dependencies: 509
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_dept_dept_id_seq OWNED BY basicdb_flowable7.sys_dept.dept_id;


--
-- TOC entry 510 (class 1259 OID 4556451)
-- Name: sys_dict_data; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_dict_data (
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


ALTER TABLE basicdb_flowable7.sys_dict_data OWNER TO postgres;

--
-- TOC entry 511 (class 1259 OID 4556459)
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_dict_data_dict_code_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_dict_data_dict_code_seq OWNER TO postgres;

--
-- TOC entry 4996 (class 0 OID 0)
-- Dependencies: 511
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_dict_data_dict_code_seq OWNED BY basicdb_flowable7.sys_dict_data.dict_code;


--
-- TOC entry 512 (class 1259 OID 4556460)
-- Name: sys_dict_type; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_dict_type (
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


ALTER TABLE basicdb_flowable7.sys_dict_type OWNER TO postgres;

--
-- TOC entry 513 (class 1259 OID 4556466)
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_dict_type_dict_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_dict_type_dict_id_seq OWNER TO postgres;

--
-- TOC entry 4999 (class 0 OID 0)
-- Dependencies: 513
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_dict_type_dict_id_seq OWNED BY basicdb_flowable7.sys_dict_type.dict_id;


--
-- TOC entry 514 (class 1259 OID 4556467)
-- Name: sys_files; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.sys_files (
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
    update_time timestamp(0) without time zone
);


ALTER TABLE basicdb_flowable7.sys_files OWNER TO system;

--
-- TOC entry 515 (class 1259 OID 4556472)
-- Name: sys_i18n; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.sys_i18n (
    message_id character varying(255) NOT NULL,
    value text NOT NULL,
    language character varying(50) NOT NULL
);


ALTER TABLE basicdb_flowable7.sys_i18n OWNER TO system;

--
-- TOC entry 5001 (class 0 OID 0)
-- Dependencies: 515
-- Name: COLUMN sys_i18n.message_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.sys_i18n.message_id IS '标识';


--
-- TOC entry 5002 (class 0 OID 0)
-- Dependencies: 515
-- Name: COLUMN sys_i18n.value; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.sys_i18n.value IS '值';


--
-- TOC entry 5003 (class 0 OID 0)
-- Dependencies: 515
-- Name: COLUMN sys_i18n.language; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.sys_i18n.language IS '语言';


--
-- TOC entry 516 (class 1259 OID 4556477)
-- Name: sys_job; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_job (
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


ALTER TABLE basicdb_flowable7.sys_job OWNER TO postgres;

--
-- TOC entry 517 (class 1259 OID 4556486)
-- Name: sys_job_job_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_job_job_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_job_job_id_seq OWNER TO postgres;

--
-- TOC entry 5005 (class 0 OID 0)
-- Dependencies: 517
-- Name: sys_job_job_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_job_job_id_seq OWNED BY basicdb_flowable7.sys_job.job_id;


--
-- TOC entry 518 (class 1259 OID 4556487)
-- Name: sys_job_log; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_job_log (
    job_log_id bigint NOT NULL,
    job_name character varying(64) NOT NULL,
    job_group character varying(64) NOT NULL,
    invoke_target character varying(500) NOT NULL,
    job_message character varying(500),
    status character varying(3) DEFAULT '0'::character varying,
    exception_info character varying(2000),
    create_time timestamp(0) without time zone
);


ALTER TABLE basicdb_flowable7.sys_job_log OWNER TO postgres;

--
-- TOC entry 519 (class 1259 OID 4556493)
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_job_log_job_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_job_log_job_log_id_seq OWNER TO postgres;

--
-- TOC entry 5008 (class 0 OID 0)
-- Dependencies: 519
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_job_log_job_log_id_seq OWNED BY basicdb_flowable7.sys_job_log.job_log_id;


--
-- TOC entry 520 (class 1259 OID 4556494)
-- Name: sys_logininfor; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_logininfor (
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


ALTER TABLE basicdb_flowable7.sys_logininfor OWNER TO postgres;

--
-- TOC entry 521 (class 1259 OID 4556500)
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_logininfor_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_logininfor_info_id_seq OWNER TO postgres;

--
-- TOC entry 5011 (class 0 OID 0)
-- Dependencies: 521
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_logininfor_info_id_seq OWNED BY basicdb_flowable7.sys_logininfor.info_id;


--
-- TOC entry 522 (class 1259 OID 4556501)
-- Name: sys_menu; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_menu (
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


ALTER TABLE basicdb_flowable7.sys_menu OWNER TO postgres;

--
-- TOC entry 5013 (class 0 OID 0)
-- Dependencies: 522
-- Name: COLUMN sys_menu.show_tags; Type: COMMENT; Schema: basicdb_flowable7; Owner: postgres
--

COMMENT ON COLUMN basicdb_flowable7.sys_menu.show_tags IS '自定义配置该页面是否展示Tags-view';


--
-- TOC entry 523 (class 1259 OID 4556513)
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_menu_menu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_menu_menu_id_seq OWNER TO postgres;

--
-- TOC entry 5015 (class 0 OID 0)
-- Dependencies: 523
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_menu_menu_id_seq OWNED BY basicdb_flowable7.sys_menu.menu_id;


--
-- TOC entry 524 (class 1259 OID 4556514)
-- Name: sys_notice; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_notice (
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


ALTER TABLE basicdb_flowable7.sys_notice OWNER TO postgres;

--
-- TOC entry 525 (class 1259 OID 4556520)
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_notice_notice_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_notice_notice_id_seq OWNER TO postgres;

--
-- TOC entry 5018 (class 0 OID 0)
-- Dependencies: 525
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_notice_notice_id_seq OWNED BY basicdb_flowable7.sys_notice.notice_id;


--
-- TOC entry 526 (class 1259 OID 4556521)
-- Name: sys_notice_read; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.sys_notice_read (
    read_id bigint NOT NULL,
    notice_id bigint NOT NULL,
    user_id bigint NOT NULL,
    read_time timestamp(0) without time zone
);


ALTER TABLE basicdb_flowable7.sys_notice_read OWNER TO system;

--
-- TOC entry 527 (class 1259 OID 4556524)
-- Name: sys_notice_read_read_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: system
--

CREATE SEQUENCE basicdb_flowable7.sys_notice_read_read_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_notice_read_read_id_seq OWNER TO system;

--
-- TOC entry 5020 (class 0 OID 0)
-- Dependencies: 527
-- Name: sys_notice_read_read_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: system
--

ALTER SEQUENCE basicdb_flowable7.sys_notice_read_read_id_seq OWNED BY basicdb_flowable7.sys_notice_read.read_id;


--
-- TOC entry 528 (class 1259 OID 4556525)
-- Name: sys_oper_log; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_oper_log (
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


ALTER TABLE basicdb_flowable7.sys_oper_log OWNER TO postgres;

--
-- TOC entry 529 (class 1259 OID 4556533)
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_oper_log_oper_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_oper_log_oper_id_seq OWNER TO postgres;

--
-- TOC entry 5022 (class 0 OID 0)
-- Dependencies: 529
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_oper_log_oper_id_seq OWNED BY basicdb_flowable7.sys_oper_log.oper_id;


--
-- TOC entry 530 (class 1259 OID 4556534)
-- Name: sys_post; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_post (
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


ALTER TABLE basicdb_flowable7.sys_post OWNER TO postgres;

--
-- TOC entry 531 (class 1259 OID 4556539)
-- Name: sys_post_post_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_post_post_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_post_post_id_seq OWNER TO postgres;

--
-- TOC entry 5025 (class 0 OID 0)
-- Dependencies: 531
-- Name: sys_post_post_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_post_post_id_seq OWNED BY basicdb_flowable7.sys_post.post_id;


--
-- TOC entry 532 (class 1259 OID 4556540)
-- Name: sys_role; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_role (
    role_id bigint NOT NULL,
    role_name character varying(30) NOT NULL,
    role_key character varying(100) NOT NULL,
    role_sort integer NOT NULL,
    data_scope character varying(3) DEFAULT '1'::character varying,
    menu_check_strictly smallint DEFAULT 1,
    dept_check_strictly smallint DEFAULT 1,
    status character varying(3) NOT NULL,
    del_flag character varying(3) DEFAULT '0'::character varying,
    create_by character varying(64),
    create_time timestamp(0) without time zone,
    update_by character varying(64),
    update_time timestamp(0) without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable7.sys_role OWNER TO postgres;

--
-- TOC entry 533 (class 1259 OID 4556549)
-- Name: sys_role_dept; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_role_dept (
    role_id bigint NOT NULL,
    dept_id bigint NOT NULL
);


ALTER TABLE basicdb_flowable7.sys_role_dept OWNER TO postgres;

--
-- TOC entry 534 (class 1259 OID 4556552)
-- Name: sys_role_menu; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_role_menu (
    role_id bigint NOT NULL,
    menu_id bigint NOT NULL
);


ALTER TABLE basicdb_flowable7.sys_role_menu OWNER TO postgres;

--
-- TOC entry 535 (class 1259 OID 4556555)
-- Name: sys_role_role_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_role_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_role_role_id_seq OWNER TO postgres;

--
-- TOC entry 5030 (class 0 OID 0)
-- Dependencies: 535
-- Name: sys_role_role_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_role_role_id_seq OWNED BY basicdb_flowable7.sys_role.role_id;


--
-- TOC entry 536 (class 1259 OID 4556556)
-- Name: sys_user; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_user (
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


ALTER TABLE basicdb_flowable7.sys_user OWNER TO postgres;

--
-- TOC entry 537 (class 1259 OID 4556565)
-- Name: sys_user_post; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_user_post (
    user_id bigint NOT NULL,
    post_id bigint NOT NULL
);


ALTER TABLE basicdb_flowable7.sys_user_post OWNER TO postgres;

--
-- TOC entry 538 (class 1259 OID 4556568)
-- Name: sys_user_role; Type: TABLE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE TABLE basicdb_flowable7.sys_user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE basicdb_flowable7.sys_user_role OWNER TO postgres;

--
-- TOC entry 539 (class 1259 OID 4556571)
-- Name: sys_user_user_id_seq; Type: SEQUENCE; Schema: basicdb_flowable7; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable7.sys_user_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable7.sys_user_user_id_seq OWNER TO postgres;

--
-- TOC entry 5035 (class 0 OID 0)
-- Dependencies: 539
-- Name: sys_user_user_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable7; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable7.sys_user_user_id_seq OWNED BY basicdb_flowable7.sys_user.user_id;


--
-- TOC entry 587 (class 1259 OID 4557560)
-- Name: tbl_flow_extend_procinst; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.tbl_flow_extend_procinst (
    id character varying(32) NOT NULL,
    proc_ins_id character varying(64),
    proc_def_id character varying(64),
    model_key character varying(128),
    model_id character varying(64),
    process_name character varying(255),
    process_status character varying(32),
    current_user_code character varying(64),
    tenant_id character varying(64),
    form_data text,
    current_name character varying(64),
    creator character varying(64),
    create_time timestamp without time zone,
    updater character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable7.tbl_flow_extend_procinst OWNER TO system;

--
-- TOC entry 5037 (class 0 OID 0)
-- Dependencies: 587
-- Name: TABLE tbl_flow_extend_procinst; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON TABLE basicdb_flowable7.tbl_flow_extend_procinst IS '流程业务拓展（实例-表单/分类关联）';


--
-- TOC entry 5038 (class 0 OID 0)
-- Dependencies: 587
-- Name: COLUMN tbl_flow_extend_procinst.proc_ins_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.tbl_flow_extend_procinst.proc_ins_id IS '流程实例ID';


--
-- TOC entry 5039 (class 0 OID 0)
-- Dependencies: 587
-- Name: COLUMN tbl_flow_extend_procinst.proc_def_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.tbl_flow_extend_procinst.proc_def_id IS '流程定义ID';


--
-- TOC entry 5040 (class 0 OID 0)
-- Dependencies: 587
-- Name: COLUMN tbl_flow_extend_procinst.model_key; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.tbl_flow_extend_procinst.model_key IS '模型key';


--
-- TOC entry 5041 (class 0 OID 0)
-- Dependencies: 587
-- Name: COLUMN tbl_flow_extend_procinst.model_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.tbl_flow_extend_procinst.model_id IS '模型ID';


--
-- TOC entry 5042 (class 0 OID 0)
-- Dependencies: 587
-- Name: COLUMN tbl_flow_extend_procinst.process_name; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.tbl_flow_extend_procinst.process_name IS '流程名称';


--
-- TOC entry 5043 (class 0 OID 0)
-- Dependencies: 587
-- Name: COLUMN tbl_flow_extend_procinst.process_status; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.tbl_flow_extend_procinst.process_status IS '流程状态';


--
-- TOC entry 5044 (class 0 OID 0)
-- Dependencies: 587
-- Name: COLUMN tbl_flow_extend_procinst.current_user_code; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.tbl_flow_extend_procinst.current_user_code IS '发起人ID';


--
-- TOC entry 5045 (class 0 OID 0)
-- Dependencies: 587
-- Name: COLUMN tbl_flow_extend_procinst.tenant_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.tbl_flow_extend_procinst.tenant_id IS '租户ID（业务系统标识）';


--
-- TOC entry 5046 (class 0 OID 0)
-- Dependencies: 587
-- Name: COLUMN tbl_flow_extend_procinst.form_data; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.tbl_flow_extend_procinst.form_data IS '表单数据JSON';


--
-- TOC entry 5047 (class 0 OID 0)
-- Dependencies: 587
-- Name: COLUMN tbl_flow_extend_procinst.current_name; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.tbl_flow_extend_procinst.current_name IS '发起人名称';


--
-- TOC entry 578 (class 1259 OID 4557476)
-- Name: wf_category; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.wf_category (
    category_id bigint NOT NULL,
    parent bigint DEFAULT 0,
    category_name character varying(64) NOT NULL,
    code character varying(64),
    url character varying(255),
    remark character varying(500),
    del_flag character varying(2) DEFAULT '0'::character varying,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


ALTER TABLE basicdb_flowable7.wf_category OWNER TO system;

--
-- TOC entry 5048 (class 0 OID 0)
-- Dependencies: 578
-- Name: TABLE wf_category; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON TABLE basicdb_flowable7.wf_category IS '流程分类';


--
-- TOC entry 5049 (class 0 OID 0)
-- Dependencies: 578
-- Name: COLUMN wf_category.category_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_category.category_id IS '分类ID';


--
-- TOC entry 5050 (class 0 OID 0)
-- Dependencies: 578
-- Name: COLUMN wf_category.parent; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_category.parent IS '父级分类ID（无父级为0）';


--
-- TOC entry 5051 (class 0 OID 0)
-- Dependencies: 578
-- Name: COLUMN wf_category.category_name; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_category.category_name IS '分类名称';


--
-- TOC entry 5052 (class 0 OID 0)
-- Dependencies: 578
-- Name: COLUMN wf_category.code; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_category.code IS '分类编码';


--
-- TOC entry 5053 (class 0 OID 0)
-- Dependencies: 578
-- Name: COLUMN wf_category.url; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_category.url IS '跳转地址（业务表单路由）';


--
-- TOC entry 5054 (class 0 OID 0)
-- Dependencies: 578
-- Name: COLUMN wf_category.del_flag; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_category.del_flag IS '删除标志（0存在 2删除）';


--
-- TOC entry 586 (class 1259 OID 4557550)
-- Name: wf_comment_info; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.wf_comment_info (
    id character varying(32) NOT NULL,
    type character varying(32),
    user_id character varying(64),
    "time" timestamp without time zone,
    task_id character varying(64),
    activity_id character varying(64),
    activity_name character varying(128),
    proc_ins_id character varying(64),
    action character varying(32),
    message text,
    del_flag character varying(2) DEFAULT '0'::character varying,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable7.wf_comment_info OWNER TO system;

--
-- TOC entry 5055 (class 0 OID 0)
-- Dependencies: 586
-- Name: TABLE wf_comment_info; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON TABLE basicdb_flowable7.wf_comment_info IS '流程意见';


--
-- TOC entry 5056 (class 0 OID 0)
-- Dependencies: 586
-- Name: COLUMN wf_comment_info.id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_comment_info.id IS '主键（ASSIGN_UUID）';


--
-- TOC entry 5057 (class 0 OID 0)
-- Dependencies: 586
-- Name: COLUMN wf_comment_info.type; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_comment_info.type IS '意见类型';


--
-- TOC entry 5058 (class 0 OID 0)
-- Dependencies: 586
-- Name: COLUMN wf_comment_info.user_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_comment_info.user_id IS '处理人ID';


--
-- TOC entry 5059 (class 0 OID 0)
-- Dependencies: 586
-- Name: COLUMN wf_comment_info."time"; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_comment_info."time" IS '审批时间';


--
-- TOC entry 5060 (class 0 OID 0)
-- Dependencies: 586
-- Name: COLUMN wf_comment_info.task_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_comment_info.task_id IS '任务ID';


--
-- TOC entry 5061 (class 0 OID 0)
-- Dependencies: 586
-- Name: COLUMN wf_comment_info.activity_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_comment_info.activity_id IS '活动ID';


--
-- TOC entry 5062 (class 0 OID 0)
-- Dependencies: 586
-- Name: COLUMN wf_comment_info.activity_name; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_comment_info.activity_name IS '活动名称';


--
-- TOC entry 5063 (class 0 OID 0)
-- Dependencies: 586
-- Name: COLUMN wf_comment_info.proc_ins_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_comment_info.proc_ins_id IS '流程实例ID';


--
-- TOC entry 5064 (class 0 OID 0)
-- Dependencies: 586
-- Name: COLUMN wf_comment_info.action; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_comment_info.action IS '动作';


--
-- TOC entry 5065 (class 0 OID 0)
-- Dependencies: 586
-- Name: COLUMN wf_comment_info.message; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_comment_info.message IS '审批意见';


--
-- TOC entry 5066 (class 0 OID 0)
-- Dependencies: 586
-- Name: COLUMN wf_comment_info.del_flag; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_comment_info.del_flag IS '删除标志';


--
-- TOC entry 585 (class 1259 OID 4557540)
-- Name: wf_copy; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.wf_copy (
    copy_id bigint NOT NULL,
    title character varying(255),
    process_id character varying(64),
    process_name character varying(255),
    category_id character varying(64),
    deployment_id character varying(64),
    instance_id character varying(64),
    task_id character varying(64),
    user_id character varying(64),
    originator_id character varying(64),
    originator_name character varying(64),
    status character varying(2) DEFAULT '0'::character varying,
    activity_id character varying(64),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable7.wf_copy OWNER TO system;

--
-- TOC entry 5067 (class 0 OID 0)
-- Dependencies: 585
-- Name: TABLE wf_copy; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON TABLE basicdb_flowable7.wf_copy IS '流程抄送';


--
-- TOC entry 5068 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.copy_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.copy_id IS '抄送主键';


--
-- TOC entry 5069 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.title; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.title IS '抄送标题';


--
-- TOC entry 5070 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.process_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.process_id IS '流程主键';


--
-- TOC entry 5071 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.process_name; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.process_name IS '流程名称';


--
-- TOC entry 5072 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.category_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.category_id IS '流程分类编码';


--
-- TOC entry 5073 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.deployment_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.deployment_id IS '部署主键';


--
-- TOC entry 5074 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.instance_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.instance_id IS '流程实例主键';


--
-- TOC entry 5075 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.task_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.task_id IS '任务主键';


--
-- TOC entry 5076 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.user_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.user_id IS '抄送人ID';


--
-- TOC entry 5077 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.originator_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.originator_id IS '发起人ID';


--
-- TOC entry 5078 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.originator_name; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.originator_name IS '发起人名称';


--
-- TOC entry 5079 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.status; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.status IS '状态（0未阅 1已阅）';


--
-- TOC entry 5080 (class 0 OID 0)
-- Dependencies: 585
-- Name: COLUMN wf_copy.activity_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_copy.activity_id IS '活动ID';


--
-- TOC entry 583 (class 1259 OID 4557521)
-- Name: wf_deploy_form; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.wf_deploy_form (
    id character varying(32) NOT NULL,
    deploy_id character varying(64),
    form_id character varying(32),
    activity_id character varying(64),
    model_key character varying(128),
    form_name character varying(128),
    activity_name character varying(128),
    activity_type character varying(32),
    content text
);


ALTER TABLE basicdb_flowable7.wf_deploy_form OWNER TO system;

--
-- TOC entry 5081 (class 0 OID 0)
-- Dependencies: 583
-- Name: TABLE wf_deploy_form; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON TABLE basicdb_flowable7.wf_deploy_form IS '流程部署与表单关联';


--
-- TOC entry 5082 (class 0 OID 0)
-- Dependencies: 583
-- Name: COLUMN wf_deploy_form.id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_deploy_form.id IS '主键（ASSIGN_UUID）';


--
-- TOC entry 5083 (class 0 OID 0)
-- Dependencies: 583
-- Name: COLUMN wf_deploy_form.deploy_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_deploy_form.deploy_id IS '流程部署主键';


--
-- TOC entry 5084 (class 0 OID 0)
-- Dependencies: 583
-- Name: COLUMN wf_deploy_form.form_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_deploy_form.form_id IS '表单ID';


--
-- TOC entry 5085 (class 0 OID 0)
-- Dependencies: 583
-- Name: COLUMN wf_deploy_form.activity_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_deploy_form.activity_id IS '节点Key';


--
-- TOC entry 5086 (class 0 OID 0)
-- Dependencies: 583
-- Name: COLUMN wf_deploy_form.model_key; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_deploy_form.model_key IS '模型Key';


--
-- TOC entry 5087 (class 0 OID 0)
-- Dependencies: 583
-- Name: COLUMN wf_deploy_form.form_name; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_deploy_form.form_name IS '表单名称';


--
-- TOC entry 5088 (class 0 OID 0)
-- Dependencies: 583
-- Name: COLUMN wf_deploy_form.activity_name; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_deploy_form.activity_name IS '节点名称';


--
-- TOC entry 5089 (class 0 OID 0)
-- Dependencies: 583
-- Name: COLUMN wf_deploy_form.activity_type; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_deploy_form.activity_type IS '节点类型';


--
-- TOC entry 5090 (class 0 OID 0)
-- Dependencies: 583
-- Name: COLUMN wf_deploy_form.content; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_deploy_form.content IS '表单内容（运行时快照）';


--
-- TOC entry 580 (class 1259 OID 4557495)
-- Name: wf_form; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.wf_form (
    form_id character varying(32) NOT NULL,
    form_name character varying(128) NOT NULL,
    content text,
    code character varying(64),
    status character varying(16),
    type character varying(32),
    category_id character varying(32),
    remark character varying(500),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar,
    version integer DEFAULT 1
);


ALTER TABLE basicdb_flowable7.wf_form OWNER TO system;

--
-- TOC entry 5091 (class 0 OID 0)
-- Dependencies: 580
-- Name: TABLE wf_form; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON TABLE basicdb_flowable7.wf_form IS '自定义表单模板';


--
-- TOC entry 5092 (class 0 OID 0)
-- Dependencies: 580
-- Name: COLUMN wf_form.form_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form.form_id IS '表单主键（ASSIGN_UUID 32位）';


--
-- TOC entry 5093 (class 0 OID 0)
-- Dependencies: 580
-- Name: COLUMN wf_form.form_name; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form.form_name IS '表单名称';


--
-- TOC entry 5094 (class 0 OID 0)
-- Dependencies: 580
-- Name: COLUMN wf_form.content; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form.content IS '表单内容（JSON）';


--
-- TOC entry 5095 (class 0 OID 0)
-- Dependencies: 580
-- Name: COLUMN wf_form.code; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form.code IS '表单编码';


--
-- TOC entry 5096 (class 0 OID 0)
-- Dependencies: 580
-- Name: COLUMN wf_form.status; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form.status IS '状态';


--
-- TOC entry 5097 (class 0 OID 0)
-- Dependencies: 580
-- Name: COLUMN wf_form.type; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form.type IS '表单类型';


--
-- TOC entry 5098 (class 0 OID 0)
-- Dependencies: 580
-- Name: COLUMN wf_form.category_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form.category_id IS '分类ID';


--
-- TOC entry 5099 (class 0 OID 0)
-- Dependencies: 580
-- Name: COLUMN wf_form.del_flag; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form.del_flag IS '删除标志（0存在 2删除）';


--
-- TOC entry 5100 (class 0 OID 0)
-- Dependencies: 580
-- Name: COLUMN wf_form.version; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form.version IS '版本号';


--
-- TOC entry 581 (class 1259 OID 4557504)
-- Name: wf_form_category; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.wf_form_category (
    id character varying(32) NOT NULL,
    parent character varying(32) DEFAULT '0'::character varying,
    category_name character varying(64) NOT NULL,
    remark character varying(500),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


ALTER TABLE basicdb_flowable7.wf_form_category OWNER TO system;

--
-- TOC entry 5101 (class 0 OID 0)
-- Dependencies: 581
-- Name: TABLE wf_form_category; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON TABLE basicdb_flowable7.wf_form_category IS '自定义表单分类';


--
-- TOC entry 5102 (class 0 OID 0)
-- Dependencies: 581
-- Name: COLUMN wf_form_category.id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form_category.id IS '分类主键（ASSIGN_UUID）';


--
-- TOC entry 5103 (class 0 OID 0)
-- Dependencies: 581
-- Name: COLUMN wf_form_category.parent; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form_category.parent IS '父分类ID（无父级为0）';


--
-- TOC entry 5104 (class 0 OID 0)
-- Dependencies: 581
-- Name: COLUMN wf_form_category.category_name; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form_category.category_name IS '分类名称';


--
-- TOC entry 582 (class 1259 OID 4557513)
-- Name: wf_form_template; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.wf_form_template (
    form_id character varying(32) NOT NULL,
    form_name character varying(128) NOT NULL,
    content text,
    img_url character varying(255),
    del_flag character varying(2) DEFAULT '0'::character varying,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


ALTER TABLE basicdb_flowable7.wf_form_template OWNER TO system;

--
-- TOC entry 5105 (class 0 OID 0)
-- Dependencies: 582
-- Name: TABLE wf_form_template; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON TABLE basicdb_flowable7.wf_form_template IS '自定义表单模板（设计器样例）';


--
-- TOC entry 5106 (class 0 OID 0)
-- Dependencies: 582
-- Name: COLUMN wf_form_template.form_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form_template.form_id IS '模板主键（ASSIGN_UUID）';


--
-- TOC entry 5107 (class 0 OID 0)
-- Dependencies: 582
-- Name: COLUMN wf_form_template.form_name; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form_template.form_name IS '模板名称';


--
-- TOC entry 5108 (class 0 OID 0)
-- Dependencies: 582
-- Name: COLUMN wf_form_template.content; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form_template.content IS '模板内容（JSON）';


--
-- TOC entry 5109 (class 0 OID 0)
-- Dependencies: 582
-- Name: COLUMN wf_form_template.img_url; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form_template.img_url IS '示例图片URL';


--
-- TOC entry 5110 (class 0 OID 0)
-- Dependencies: 582
-- Name: COLUMN wf_form_template.del_flag; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_form_template.del_flag IS '删除标志（0存在 2删除）';


--
-- TOC entry 584 (class 1259 OID 4557530)
-- Name: wf_task_form; Type: TABLE; Schema: basicdb_flowable7; Owner: system
--

CREATE TABLE basicdb_flowable7.wf_task_form (
    id character varying(32) NOT NULL,
    form_id character varying(32),
    proc_ins_id character varying(64),
    task_id character varying(64),
    activity_id character varying(64),
    user_id character varying(64),
    form_content text,
    field character varying(128),
    field_value text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


ALTER TABLE basicdb_flowable7.wf_task_form OWNER TO system;

--
-- TOC entry 5111 (class 0 OID 0)
-- Dependencies: 584
-- Name: TABLE wf_task_form; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON TABLE basicdb_flowable7.wf_task_form IS '流程任务表单快照';


--
-- TOC entry 5112 (class 0 OID 0)
-- Dependencies: 584
-- Name: COLUMN wf_task_form.id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_task_form.id IS '主键（ASSIGN_UUID）';


--
-- TOC entry 5113 (class 0 OID 0)
-- Dependencies: 584
-- Name: COLUMN wf_task_form.form_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_task_form.form_id IS '表单ID';


--
-- TOC entry 5114 (class 0 OID 0)
-- Dependencies: 584
-- Name: COLUMN wf_task_form.proc_ins_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_task_form.proc_ins_id IS '流程实例ID';


--
-- TOC entry 5115 (class 0 OID 0)
-- Dependencies: 584
-- Name: COLUMN wf_task_form.task_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_task_form.task_id IS '任务ID';


--
-- TOC entry 5116 (class 0 OID 0)
-- Dependencies: 584
-- Name: COLUMN wf_task_form.activity_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_task_form.activity_id IS '活动/节点ID';


--
-- TOC entry 5117 (class 0 OID 0)
-- Dependencies: 584
-- Name: COLUMN wf_task_form.user_id; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_task_form.user_id IS '处理人ID';


--
-- TOC entry 5118 (class 0 OID 0)
-- Dependencies: 584
-- Name: COLUMN wf_task_form.form_content; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_task_form.form_content IS '本次提交的表单数据（JSON）';


--
-- TOC entry 5119 (class 0 OID 0)
-- Dependencies: 584
-- Name: COLUMN wf_task_form.field; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_task_form.field IS '兼容列：早期EAV字段名，新数据不写';


--
-- TOC entry 5120 (class 0 OID 0)
-- Dependencies: 584
-- Name: COLUMN wf_task_form.field_value; Type: COMMENT; Schema: basicdb_flowable7; Owner: system
--

COMMENT ON COLUMN basicdb_flowable7.wf_task_form.field_value IS '兼容列：早期EAV字段值，新数据不写';


--
-- TOC entry 4336 (class 2604 OID 4557065)
-- Name: act_evt_log log_nr_; Type: DEFAULT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_evt_log ALTER COLUMN log_nr_ SET DEFAULT nextval('basicdb_flowable7.act_evt_log_log_nr__seq'::regclass);


--
-- TOC entry 4327 (class 2604 OID 4556980)
-- Name: act_hi_tsk_log id_; Type: DEFAULT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_hi_tsk_log ALTER COLUMN id_ SET DEFAULT nextval('basicdb_flowable7.act_hi_tsk_log_id__seq'::regclass);


--
-- TOC entry 4263 (class 2604 OID 4556572)
-- Name: gen_table table_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.gen_table ALTER COLUMN table_id SET DEFAULT nextval('basicdb_flowable7.gen_table_table_id_seq'::regclass);


--
-- TOC entry 4267 (class 2604 OID 4556573)
-- Name: gen_table_column column_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.gen_table_column ALTER COLUMN column_id SET DEFAULT nextval('basicdb_flowable7.gen_table_column_column_id_seq'::regclass);


--
-- TOC entry 4269 (class 2604 OID 4556574)
-- Name: sys_config config_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_config ALTER COLUMN config_id SET DEFAULT nextval('basicdb_flowable7.sys_config_config_id_seq'::regclass);


--
-- TOC entry 4271 (class 2604 OID 4556575)
-- Name: sys_dept dept_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_dept ALTER COLUMN dept_id SET DEFAULT nextval('basicdb_flowable7.sys_dept_dept_id_seq'::regclass);


--
-- TOC entry 4276 (class 2604 OID 4556576)
-- Name: sys_dict_data dict_code; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_dict_data ALTER COLUMN dict_code SET DEFAULT nextval('basicdb_flowable7.sys_dict_data_dict_code_seq'::regclass);


--
-- TOC entry 4280 (class 2604 OID 4556577)
-- Name: sys_dict_type dict_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_dict_type ALTER COLUMN dict_id SET DEFAULT nextval('basicdb_flowable7.sys_dict_type_dict_id_seq'::regclass);


--
-- TOC entry 4282 (class 2604 OID 4556578)
-- Name: sys_job job_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_job ALTER COLUMN job_id SET DEFAULT nextval('basicdb_flowable7.sys_job_job_id_seq'::regclass);


--
-- TOC entry 4287 (class 2604 OID 4556579)
-- Name: sys_job_log job_log_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_job_log ALTER COLUMN job_log_id SET DEFAULT nextval('basicdb_flowable7.sys_job_log_job_log_id_seq'::regclass);


--
-- TOC entry 4289 (class 2604 OID 4556580)
-- Name: sys_logininfor info_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_logininfor ALTER COLUMN info_id SET DEFAULT nextval('basicdb_flowable7.sys_logininfor_info_id_seq'::regclass);


--
-- TOC entry 4291 (class 2604 OID 4556581)
-- Name: sys_menu menu_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_menu ALTER COLUMN menu_id SET DEFAULT nextval('basicdb_flowable7.sys_menu_menu_id_seq'::regclass);


--
-- TOC entry 4299 (class 2604 OID 4556582)
-- Name: sys_notice notice_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_notice ALTER COLUMN notice_id SET DEFAULT nextval('basicdb_flowable7.sys_notice_notice_id_seq'::regclass);


--
-- TOC entry 4301 (class 2604 OID 4556583)
-- Name: sys_oper_log oper_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_oper_log ALTER COLUMN oper_id SET DEFAULT nextval('basicdb_flowable7.sys_oper_log_oper_id_seq'::regclass);


--
-- TOC entry 4305 (class 2604 OID 4556584)
-- Name: sys_post post_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_post ALTER COLUMN post_id SET DEFAULT nextval('basicdb_flowable7.sys_post_post_id_seq'::regclass);


--
-- TOC entry 4306 (class 2604 OID 4556585)
-- Name: sys_role role_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_role ALTER COLUMN role_id SET DEFAULT nextval('basicdb_flowable7.sys_role_role_id_seq'::regclass);


--
-- TOC entry 4311 (class 2604 OID 4556586)
-- Name: sys_user user_id; Type: DEFAULT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_user ALTER COLUMN user_id SET DEFAULT nextval('basicdb_flowable7.sys_user_user_id_seq'::regclass);


--
-- TOC entry 4935 (class 0 OID 4557062)
-- Dependencies: 566
-- Data for Name: act_evt_log; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4910 (class 0 OID 4556752)
-- Dependencies: 541
-- Data for Name: act_ge_bytearray; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4909 (class 0 OID 4556747)
-- Dependencies: 540
-- Data for Name: act_ge_property; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--

INSERT INTO basicdb_flowable7.act_ge_property VALUES ('common.schema.version', '7.2.0.2', 1);
INSERT INTO basicdb_flowable7.act_ge_property VALUES ('next.dbid', '1', 1);
INSERT INTO basicdb_flowable7.act_ge_property VALUES ('schema.version', '7.2.0.2', 1);
INSERT INTO basicdb_flowable7.act_ge_property VALUES ('schema.history', 'create(7.2.0.2)', 1);
INSERT INTO basicdb_flowable7.act_ge_property VALUES ('eventregistry.schema.version', '7.2.0.2', 1);
INSERT INTO basicdb_flowable7.act_ge_property VALUES ('cfg.execution-related-entities-count', 'true', 1);
INSERT INTO basicdb_flowable7.act_ge_property VALUES ('cfg.task-related-entities-count', 'true', 1);


--
-- TOC entry 4939 (class 0 OID 4557296)
-- Dependencies: 570
-- Data for Name: act_hi_actinst; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4942 (class 0 OID 4557319)
-- Dependencies: 573
-- Data for Name: act_hi_attachment; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4941 (class 0 OID 4557312)
-- Dependencies: 572
-- Data for Name: act_hi_comment; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4940 (class 0 OID 4557305)
-- Dependencies: 571
-- Data for Name: act_hi_detail; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4912 (class 0 OID 4556770)
-- Dependencies: 543
-- Data for Name: act_hi_entitylink; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4914 (class 0 OID 4556793)
-- Dependencies: 545
-- Data for Name: act_hi_identitylink; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4938 (class 0 OID 4557285)
-- Dependencies: 569
-- Data for Name: act_hi_procinst; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4924 (class 0 OID 4556967)
-- Dependencies: 555
-- Data for Name: act_hi_taskinst; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4926 (class 0 OID 4556977)
-- Dependencies: 557
-- Data for Name: act_hi_tsk_log; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4928 (class 0 OID 4557005)
-- Dependencies: 559
-- Data for Name: act_hi_varinst; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4936 (class 0 OID 4557071)
-- Dependencies: 567
-- Data for Name: act_procdef_info; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4930 (class 0 OID 4557028)
-- Dependencies: 561
-- Data for Name: act_re_deployment; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4931 (class 0 OID 4557036)
-- Dependencies: 562
-- Data for Name: act_re_model; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4933 (class 0 OID 4557052)
-- Dependencies: 564
-- Data for Name: act_re_procdef; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4937 (class 0 OID 4557076)
-- Dependencies: 568
-- Data for Name: act_ru_actinst; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4918 (class 0 OID 4556828)
-- Dependencies: 549
-- Data for Name: act_ru_deadletter_job; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4911 (class 0 OID 4556759)
-- Dependencies: 542
-- Data for Name: act_ru_entitylink; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4929 (class 0 OID 4557016)
-- Dependencies: 560
-- Data for Name: act_ru_event_subscr; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4932 (class 0 OID 4557044)
-- Dependencies: 563
-- Data for Name: act_ru_execution; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4920 (class 0 OID 4556844)
-- Dependencies: 551
-- Data for Name: act_ru_external_job; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4919 (class 0 OID 4556836)
-- Dependencies: 550
-- Data for Name: act_ru_history_job; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4913 (class 0 OID 4556781)
-- Dependencies: 544
-- Data for Name: act_ru_identitylink; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4915 (class 0 OID 4556804)
-- Dependencies: 546
-- Data for Name: act_ru_job; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4917 (class 0 OID 4556820)
-- Dependencies: 548
-- Data for Name: act_ru_suspended_job; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4923 (class 0 OID 4556955)
-- Dependencies: 554
-- Data for Name: act_ru_task; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4916 (class 0 OID 4556812)
-- Dependencies: 547
-- Data for Name: act_ru_timer_job; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4927 (class 0 OID 4556990)
-- Dependencies: 558
-- Data for Name: act_ru_variable; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4946 (class 0 OID 4557372)
-- Dependencies: 577
-- Data for Name: flw_channel_definition; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4945 (class 0 OID 4557358)
-- Dependencies: 576
-- Data for Name: flw_event_definition; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4943 (class 0 OID 4557344)
-- Dependencies: 574
-- Data for Name: flw_event_deployment; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4944 (class 0 OID 4557351)
-- Dependencies: 575
-- Data for Name: flw_event_resource; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4948 (class 0 OID 4557487)
-- Dependencies: 579
-- Data for Name: flw_expression; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4921 (class 0 OID 4556933)
-- Dependencies: 552
-- Data for Name: flw_ru_batch; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4922 (class 0 OID 4556941)
-- Dependencies: 553
-- Data for Name: flw_ru_batch_part; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4859 (class 0 OID 4556356)
-- Dependencies: 488
-- Data for Name: gen_table; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--

INSERT INTO basicdb_flowable7.gen_table VALUES (1, 'sys_role_dept', NULL, NULL, NULL, 'SysRoleDept', 'crud', 'com.basic.system', 'system', 'dept', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (2, 'sys_role_menu', NULL, NULL, NULL, 'SysRoleMenu', 'crud', 'com.basic.system', 'system', 'menu', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (3, 'sys_user', NULL, NULL, NULL, 'SysUser', 'crud', 'com.basic.system', 'system', 'user', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (4, 'sys_user_post', NULL, NULL, NULL, 'SysUserPost', 'crud', 'com.basic.system', 'system', 'post', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (5, 'sys_user_role', NULL, NULL, NULL, 'SysUserRole', 'crud', 'com.basic.system', 'system', 'role', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (6, 'sys_notice', NULL, NULL, NULL, 'SysNotice', 'crud', 'com.basic.system', 'system', 'notice', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (7, 'sys_menu', NULL, NULL, NULL, 'SysMenu', 'crud', 'com.basic.system', 'system', 'menu', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (8, 'sys_config', NULL, NULL, NULL, 'SysConfig', 'crud', 'com.basic.system', 'system', 'config', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (9, 'sys_dept', NULL, NULL, NULL, 'SysDept', 'crud', 'com.basic.system', 'system', 'dept', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (10, 'sys_dict_data', NULL, NULL, NULL, 'SysDictData', 'crud', 'com.basic.system', 'system', 'data', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (11, 'sys_dict_type', NULL, NULL, NULL, 'SysDictType', 'crud', 'com.basic.system', 'system', 'type', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (12, 'sys_job', NULL, NULL, NULL, 'SysJob', 'crud', 'com.basic.system', 'system', 'job', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (13, 'sys_job_log', NULL, NULL, NULL, 'SysJobLog', 'crud', 'com.basic.system', 'system', 'log', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (14, 'sys_logininfor', NULL, NULL, NULL, 'SysLogininfor', 'crud', 'com.basic.system', 'system', 'logininfor', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (15, 'sys_oper_log', NULL, NULL, NULL, 'SysOperLog', 'crud', 'com.basic.system', 'system', 'log', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (16, 'sys_post', NULL, NULL, NULL, 'SysPost', 'crud', 'com.basic.system', 'system', 'post', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (17, 'sys_role', NULL, NULL, NULL, 'SysRole', 'crud', 'com.basic.system', 'system', 'role', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table VALUES (18, 'sys_demo', NULL, NULL, NULL, 'SysDemo', 'crud', 'com.basic.system', 'system', 'demo', NULL, 'basic', '0', '/', NULL, 'admin', '2024-02-01 11:09:28', NULL, NULL, NULL);


--
-- TOC entry 4860 (class 0 OID 4556364)
-- Dependencies: 489
-- Data for Name: gen_table_column; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.gen_table_column VALUES (3, '2', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (4, '2', 'menu_id', NULL, 'bigint', 'Long', 'menuId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (5, '3', 'user_id', NULL, 'bigint', 'Long', 'userId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (6, '3', 'dept_id', NULL, 'bigint', 'Long', 'deptId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (7, '3', 'user_name', NULL, 'character varying', 'String', 'userName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 3, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (8, '3', 'nick_name', NULL, 'character varying', 'String', 'nickName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 4, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (9, '3', 'user_type', NULL, 'character varying', 'String', 'userType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 5, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (10, '3', 'email', NULL, 'character varying', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (11, '3', 'phonenumber', NULL, 'character varying', 'String', 'phonenumber', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (12, '3', 'sex', NULL, 'character varying', 'String', 'sex', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 8, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (13, '3', 'avatar', NULL, 'character varying', 'String', 'avatar', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (14, '3', 'password', NULL, 'character varying', 'String', 'password', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (15, '3', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 11, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (16, '3', 'del_flag', NULL, 'character varying', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (17, '3', 'login_ip', NULL, 'character varying', 'String', 'loginIp', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (18, '3', 'login_date', NULL, 'timestamp without time zone', 'String', 'loginDate', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (19, '3', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 15, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (20, '3', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 16, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (21, '3', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 17, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (22, '3', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 18, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (23, '3', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 19, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (24, '4', 'user_id', NULL, 'bigint', 'Long', 'userId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (25, '4', 'post_id', NULL, 'bigint', 'Long', 'postId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (26, '5', 'user_id', NULL, 'bigint', 'Long', 'userId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (27, '5', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (28, '6', 'notice_id', NULL, 'bigint', 'Long', 'noticeId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (29, '6', 'notice_title', NULL, 'character varying', 'String', 'noticeTitle', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (30, '6', 'notice_type', NULL, 'character varying', 'String', 'noticeType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', NULL, 3, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (31, '6', 'notice_content', NULL, 'bytea', 'String', 'noticeContent', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'editor', NULL, 4, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (32, '6', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 5, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (33, '6', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (34, '6', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (35, '6', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (36, '6', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (37, '6', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (38, '7', 'menu_id', NULL, 'bigint', 'Long', 'menuId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (39, '7', 'menu_name', NULL, 'character varying', 'String', 'menuName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (40, '7', 'parent_id', NULL, 'bigint', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 3, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (41, '7', 'order_num', NULL, 'integer', 'Long', 'orderNum', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 4, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (42, '7', 'path', NULL, 'character varying', 'String', 'path', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (43, '7', 'component', NULL, 'character varying', 'String', 'component', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (44, '7', 'query', NULL, 'character varying', 'String', 'query', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (45, '7', 'is_frame', NULL, 'character varying', 'String', 'isFrame', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (46, '7', 'is_cache', NULL, 'character varying', 'String', 'isCache', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (47, '7', 'menu_type', NULL, 'character varying', 'String', 'menuType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 10, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (48, '7', 'visible', NULL, 'character varying', 'String', 'visible', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (49, '7', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 12, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (50, '7', 'perms', NULL, 'character varying', 'String', 'perms', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (51, '7', 'icon', NULL, 'character varying', 'String', 'icon', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (52, '7', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 15, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (53, '7', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 16, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (54, '7', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 17, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (55, '7', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 18, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (56, '7', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 19, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (57, '8', 'config_id', NULL, 'integer', 'Long', 'configId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (58, '8', 'config_name', NULL, 'character varying', 'String', 'configName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (59, '8', 'config_key', NULL, 'character varying', 'String', 'configKey', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (60, '8', 'config_value', NULL, 'character varying', 'String', 'configValue', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (61, '8', 'config_type', NULL, 'character varying', 'String', 'configType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (62, '8', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (63, '8', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (64, '8', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (65, '8', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (66, '8', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (67, '9', 'dept_id', NULL, 'bigint', 'Long', 'deptId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (68, '9', 'parent_id', NULL, 'bigint', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (69, '9', 'ancestors', NULL, 'character varying', 'String', 'ancestors', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (70, '9', 'dept_name', NULL, 'character varying', 'String', 'deptName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (71, '9', 'order_num', NULL, 'integer', 'Long', 'orderNum', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (72, '9', 'leader', NULL, 'character varying', 'String', 'leader', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (73, '9', 'phone', NULL, 'character varying', 'String', 'phone', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (74, '9', 'email', NULL, 'character varying', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (75, '9', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (76, '9', 'del_flag', NULL, 'character varying', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (77, '9', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (78, '9', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (79, '9', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (80, '9', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (81, '10', 'dict_code', NULL, 'bigint', 'Long', 'dictCode', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (82, '10', 'dict_sort', NULL, 'integer', 'Long', 'dictSort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (83, '10', 'dict_label', NULL, 'character varying', 'String', 'dictLabel', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (84, '10', 'dict_value', NULL, 'character varying', 'String', 'dictValue', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (85, '10', 'dict_type', NULL, 'character varying', 'String', 'dictType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (86, '10', 'css_class', NULL, 'character varying', 'String', 'cssClass', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (87, '10', 'list_class', NULL, 'character varying', 'String', 'listClass', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (88, '10', 'is_default', NULL, 'character varying', 'String', 'isDefault', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (89, '10', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (90, '10', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (91, '10', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (92, '10', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (93, '10', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (94, '10', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (95, '11', 'dict_id', NULL, 'bigint', 'Long', 'dictId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (96, '11', 'dict_name', NULL, 'character varying', 'String', 'dictName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (97, '11', 'dict_type', NULL, 'character varying', 'String', 'dictType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (98, '11', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (99, '11', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (100, '11', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (101, '11', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (102, '11', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (103, '11', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (104, '12', 'job_id', NULL, 'bigint', 'Long', 'jobId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (105, '12', 'job_name', NULL, 'character varying', 'String', 'jobName', '1', '0', '0', '1', NULL, NULL, NULL, 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (106, '12', 'job_group', NULL, 'character varying', 'String', 'jobGroup', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (107, '12', 'invoke_target', NULL, 'character varying', 'String', 'invokeTarget', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (108, '12', 'cron_expression', NULL, 'character varying', 'String', 'cronExpression', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (109, '12', 'misfire_policy', NULL, 'character varying', 'String', 'misfirePolicy', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (110, '12', 'concurrent', NULL, 'character varying', 'String', 'concurrent', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (111, '12', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (112, '12', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (113, '12', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (114, '12', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (115, '12', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (116, '12', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (117, '13', 'job_log_id', NULL, 'bigint', 'Long', 'jobLogId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (118, '13', 'job_name', NULL, 'character varying', 'String', 'jobName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (119, '13', 'job_group', NULL, 'character varying', 'String', 'jobGroup', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (120, '13', 'invoke_target', NULL, 'character varying', 'String', 'invokeTarget', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (121, '13', 'job_message', NULL, 'character varying', 'String', 'jobMessage', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (122, '13', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (123, '13', 'exception_info', NULL, 'character varying', 'String', 'exceptionInfo', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (124, '13', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (125, '14', 'info_id', NULL, 'bigint', 'Long', 'infoId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (126, '14', 'user_name', NULL, 'character varying', 'String', 'userName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (127, '14', 'ipaddr', NULL, 'character varying', 'String', 'ipaddr', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (128, '14', 'login_location', NULL, 'character varying', 'String', 'loginLocation', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (129, '14', 'browser', NULL, 'character varying', 'String', 'browser', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (130, '14', 'os', NULL, 'character varying', 'String', 'os', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (131, '14', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (132, '14', 'msg', NULL, 'character varying', 'String', 'msg', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (133, '14', 'login_time', NULL, 'timestamp without time zone', 'String', 'loginTime', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (134, '15', 'oper_id', NULL, 'bigint', 'Long', 'operId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (135, '15', 'title', NULL, 'character varying', 'String', 'title', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (136, '15', 'business_type', NULL, 'integer', 'Long', 'businessType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (137, '15', 'method', NULL, 'character varying', 'String', 'method', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (138, '15', 'request_method', NULL, 'character varying', 'String', 'requestMethod', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (139, '15', 'operator_type', NULL, 'integer', 'Long', 'operatorType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (140, '15', 'oper_name', NULL, 'character varying', 'String', 'operName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (141, '15', 'dept_name', NULL, 'character varying', 'String', 'deptName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (142, '15', 'oper_url', NULL, 'character varying', 'String', 'operUrl', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (143, '15', 'oper_ip', NULL, 'character varying', 'String', 'operIp', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (144, '15', 'oper_location', NULL, 'character varying', 'String', 'operLocation', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (145, '15', 'oper_param', NULL, 'character varying', 'String', 'operParam', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (146, '15', 'json_result', NULL, 'character varying', 'String', 'jsonResult', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (147, '15', 'status', NULL, 'integer', 'Long', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (148, '15', 'error_msg', NULL, 'character varying', 'String', 'errorMsg', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 15, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (149, '15', 'oper_time', NULL, 'timestamp without time zone', 'String', 'operTime', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 16, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (150, '16', 'post_id', NULL, 'bigint', 'Long', 'postId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (151, '16', 'post_code', NULL, 'character varying', 'String', 'postCode', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (152, '16', 'post_name', NULL, 'character varying', 'String', 'postName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (153, '16', 'post_sort', NULL, 'integer', 'Long', 'postSort', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (154, '16', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (155, '16', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (156, '16', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (157, '16', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (158, '16', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (159, '16', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (160, '17', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (161, '17', 'role_name', NULL, 'character varying', 'String', 'roleName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (162, '17', 'role_key', NULL, 'character varying', 'String', 'roleKey', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (163, '17', 'role_sort', NULL, 'integer', 'Long', 'roleSort', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (164, '17', 'data_scope', NULL, 'character varying', 'String', 'dataScope', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (165, '17', 'menu_check_strictly', NULL, 'smallint', 'Long', 'menuCheckStrictly', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (166, '17', 'dept_check_strictly', NULL, 'smallint', 'Long', 'deptCheckStrictly', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (167, '17', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (168, '17', 'del_flag', NULL, 'character varying', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (169, '17', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (170, '17', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (171, '17', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (172, '17', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (173, '17', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (1, '1', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, '2024-01-16 11:33:08');
INSERT INTO basicdb_flowable7.gen_table_column VALUES (2, '1', 'dept_id', NULL, 'bigint', 'Long', 'deptId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, '2024-01-16 11:33:08');
INSERT INTO basicdb_flowable7.gen_table_column VALUES (174, '18', 'id', NULL, 'bigint', 'Long', 'id', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (175, '18', 'name', NULL, 'character varying', 'String', 'name', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (176, '18', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 3, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (177, '18', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 4, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (178, '18', 'create_id', NULL, 'bigint', 'Long', 'createId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 5, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (179, '18', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (180, '18', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (181, '18', 'update_id', NULL, 'bigint', 'Long', 'updateId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 8, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (182, '18', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 9, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (183, '18', 'is_del', NULL, 'character varying', 'String', 'isDel', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 10, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (184, '18', 'sex', NULL, 'character varying', 'String', 'sex', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 11, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable7.gen_table_column VALUES (185, '18', 'birth', NULL, 'timestamp without time zone', 'String', 'birth', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 12, 'admin', '2024-02-01 11:09:28', NULL, NULL);


--
-- TOC entry 4863 (class 0 OID 4556382)
-- Dependencies: 494
-- Data for Name: qrtz_blob_triggers; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4864 (class 0 OID 4556387)
-- Dependencies: 495
-- Data for Name: qrtz_calendars; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4865 (class 0 OID 4556392)
-- Dependencies: 496
-- Data for Name: qrtz_cron_triggers; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4866 (class 0 OID 4556397)
-- Dependencies: 497
-- Data for Name: qrtz_fired_triggers; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4867 (class 0 OID 4556402)
-- Dependencies: 498
-- Data for Name: qrtz_job_details; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4868 (class 0 OID 4556407)
-- Dependencies: 499
-- Data for Name: qrtz_locks; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4869 (class 0 OID 4556410)
-- Dependencies: 500
-- Data for Name: qrtz_paused_trigger_grps; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4870 (class 0 OID 4556413)
-- Dependencies: 501
-- Data for Name: qrtz_scheduler_state; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4871 (class 0 OID 4556416)
-- Dependencies: 502
-- Data for Name: qrtz_simple_triggers; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4872 (class 0 OID 4556421)
-- Dependencies: 503
-- Data for Name: qrtz_simprop_triggers; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4873 (class 0 OID 4556426)
-- Dependencies: 504
-- Data for Name: qrtz_triggers; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4874 (class 0 OID 4556431)
-- Dependencies: 505
-- Data for Name: sys_config; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_config VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO basicdb_flowable7.sys_config VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO basicdb_flowable7.sys_config VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO basicdb_flowable7.sys_config VALUES (10, '账号自助-密码有效期天数', 'sys.account.checkPassword.time', '90', 'Y', 'admin', '2024-02-21 11:21:18', 'admin', '2024-10-11 10:14:33', '对90天未修改密码的账号进行提醒');
INSERT INTO basicdb_flowable7.sys_config VALUES (4, '账号自助-验证码开关', 'sys.account.captchaOnOff', 'true', 'Y', 'admin', '2023-02-28 17:09:30', 'admin', '2024-01-31 17:09:54', '是否开启验证码功能（true开启，false关闭）');
INSERT INTO basicdb_flowable7.sys_config VALUES (7, '密码复杂度验证表达式', 'sys.password.inputPattern', '/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[`!@#$%^&*(\)-//.,;'':"{}\[\]\\|]).{8,15}$/', 'Y', 'admin', '2024-02-20 15:00:42', 'admin', '2024-02-20 15:55:26', '/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[`!@#$%^&*(\)-//.,;'':"{}\[\]\\|]).{8,15}$/');
INSERT INTO basicdb_flowable7.sys_config VALUES (8, '密码复杂度验证提示信息', 'sys.password.errorMessage', '用户密码长度必须介于 8 和 15 之间且包含大小写字母、数字及特殊字符', 'Y', 'admin', '2024-02-20 15:11:04', 'admin', '2024-02-20 16:03:43', NULL);
INSERT INTO basicdb_flowable7.sys_config VALUES (9, '账号自助-密码修改提醒', 'sys.account.checkPassword', 'true', 'Y', 'admin', '2024-02-21 10:59:19', NULL, NULL, '是否开启修改密码提醒(对长时间没有修改密码的账号)（true开启，false关闭）');
INSERT INTO basicdb_flowable7.sys_config VALUES (2, '账号管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '初始化密码 123456');
INSERT INTO basicdb_flowable7.sys_config VALUES (11, '账号登录-是否开启账号锁定', 'user.password.lockOpen', 'true', 'Y', 'admin', '2024-02-21 11:23:26', NULL, NULL, '是否开启密码错误次数过多账号锁定（true开启，false关闭）');
INSERT INTO basicdb_flowable7.sys_config VALUES (12, '账号登录-密码错误次数锁定', 'user.password.maxRetryCount ', '3', 'Y', 'admin', '2024-02-21 11:25:02', NULL, NULL, '账号登录时密码最大错误次数');
INSERT INTO basicdb_flowable7.sys_config VALUES (13, '账号登录-账号锁定时间', 'user.password.lockTime', '30', 'Y', 'admin', '2024-02-21 11:26:27', NULL, NULL, '账号锁定时长（分钟）');


--
-- TOC entry 4876 (class 0 OID 4556438)
-- Dependencies: 507
-- Data for Name: sys_demo; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--

INSERT INTO basicdb_flowable7.sys_demo VALUES (1750711753414205442, '李四', 'admin', '2024-01-26 10:46:23', 1, 'admin', '2024-02-19 11:34:34.706', NULL, NULL, '0', 'boy', '2024-01-09 00:00:00');


--
-- TOC entry 4877 (class 0 OID 4556441)
-- Dependencies: 508
-- Data for Name: sys_dept; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_dept VALUES (103, 101, '0,100,101', '研发部门', 1, '部门管理员', '15888888888', 'bdvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-07-10 17:08:12', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_dept VALUES (101, 100, '0,100', '佛山总公司', 1, '佛山管理员', '18888888888', 'bdvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-01-26 14:39:26', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_dept VALUES (100, 0, '0', '集团公司', 0, '管理员', '18888888888', 'dbvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-02-08 08:49:44', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_dept VALUES (102, 100, '0,100', '长沙分公司', 2, '长沙管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_dept VALUES (104, 101, '0,100,101', '市场部门', 2, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_dept VALUES (105, 101, '0,100,101', '测试部门', 3, '部门管理员', '15888888888', 'bdvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-02-08 08:49:31', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_dept VALUES (106, 101, '0,100,101', '财务部门', 4, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_dept VALUES (107, 101, '0,100,101', '运维部门', 5, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_dept VALUES (108, 102, '0,100,102', '市场部门', 1, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_dept VALUES (109, 102, '0,100,102', '财务部门', 2, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_dept VALUES (111, 101, '0,100,101', '办公室', 3, NULL, NULL, NULL, '0', '0', 'xiaoxi', '2024-04-22 16:04:38', NULL, NULL, NULL, NULL, NULL);


--
-- TOC entry 4879 (class 0 OID 4556451)
-- Dependencies: 510
-- Data for Name: sys_dict_data; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_dict_data VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '性别男');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '性别女');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '性别未知');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '显示菜单');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '隐藏菜单');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '停用状态');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '停用状态');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '默认分组');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统分组');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统默认是');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统默认否');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '通知');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '公告');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '关闭状态');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (18, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '新增操作');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (19, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '修改操作');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (20, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '删除操作');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (21, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '授权操作');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (22, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '导出操作');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (23, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '导入操作');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (24, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '强退操作');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (25, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '生成操作');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (26, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '清空操作');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (27, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (28, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '停用状态');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (30, 0, '跟随系统', '0', 'sys_show_tags', NULL, 'default', 'N', '0', 'admin', '2024-06-25 11:15:44', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (31, 1, '显示', '1', 'sys_show_tags', NULL, 'default', 'N', '0', 'admin', '2024-06-25 11:16:01', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (32, 2, '隐藏', '2', 'sys_show_tags', NULL, 'default', 'N', '0', 'admin', '2024-06-25 11:17:07', 'admin', '2024-06-25 11:17:13', NULL);
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (33, 1, '同意', 'approve', 'wf_process_permission', NULL, 'primary', 'N', '0', 'admin', '2026-10-23 16:00:00', NULL, NULL, '同意当前节点');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (34, 2, '驳回', 'reject', 'wf_process_permission', NULL, 'danger', 'N', '0', 'admin', '2026-10-23 16:00:00', NULL, NULL, '驳回到上一节点或发起人');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (35, 3, '退回', 'back', 'wf_process_permission', NULL, 'warning', 'N', '0', 'admin', '2026-10-23 16:00:00', NULL, NULL, '任意节点退回');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (36, 4, '转办', 'transfer', 'wf_process_permission', NULL, 'info', 'N', '0', 'admin', '2026-10-23 16:00:00', NULL, NULL, '转办给其他人');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (37, 5, '委派', 'delegate', 'wf_process_permission', NULL, 'info', 'N', '0', 'admin', '2026-10-23 16:00:00', NULL, NULL, '委派（任务完成后回到当前用户）');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (38, 6, '会签回退', 'countersignBack', 'wf_process_permission', NULL, 'warning', 'N', '0', 'admin', '2026-10-23 16:00:00', NULL, NULL, '会签场景的回退');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (39, 7, '前加签', 'beforeAddSign', 'wf_process_permission', NULL, 'success', 'N', '0', 'admin', '2026-10-23 16:00:00', NULL, NULL, '当前节点前加签');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (40, 8, '后加签', 'afterAddSign', 'wf_process_permission', NULL, 'success', 'N', '0', 'admin', '2026-10-23 16:00:00', NULL, NULL, '当前节点后加签');
INSERT INTO basicdb_flowable7.sys_dict_data VALUES (41, 9, '终止', 'stop', 'wf_process_permission', NULL, 'danger', 'N', '0', 'admin', '2026-10-23 16:00:00', NULL, NULL, '结束整个流程（历史/审批意见/表单数据保留）');


--
-- TOC entry 4881 (class 0 OID 4556460)
-- Dependencies: 512
-- Data for Name: sys_dict_type; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_dict_type VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '用户性别列表');
INSERT INTO basicdb_flowable7.sys_dict_type VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '菜单状态列表');
INSERT INTO basicdb_flowable7.sys_dict_type VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统开关列表');
INSERT INTO basicdb_flowable7.sys_dict_type VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '任务状态列表');
INSERT INTO basicdb_flowable7.sys_dict_type VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '任务分组列表');
INSERT INTO basicdb_flowable7.sys_dict_type VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统是否列表');
INSERT INTO basicdb_flowable7.sys_dict_type VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '通知类型列表');
INSERT INTO basicdb_flowable7.sys_dict_type VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '通知状态列表');
INSERT INTO basicdb_flowable7.sys_dict_type VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '操作类型列表');
INSERT INTO basicdb_flowable7.sys_dict_type VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '登录状态列表');
INSERT INTO basicdb_flowable7.sys_dict_type VALUES (12, '选项卡状态', 'sys_show_tags', '0', 'admin', '2024-06-25 11:14:52', 'admin', '2024-06-25 11:14:59', '自定义展示系统Tags-View组件');
INSERT INTO basicdb_flowable7.sys_dict_type VALUES (13, '流程授权功能', 'wf_process_permission', '0', 'admin', '2026-10-23 16:39:14', NULL, NULL, '流程节点上的按钮授权字典（同意/驳回/退回/转办/委派/会签回退/前加签/后加签/终止）');


--
-- TOC entry 4883 (class 0 OID 4556467)
-- Dependencies: 514
-- Data for Name: sys_files; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4884 (class 0 OID 4556472)
-- Dependencies: 515
-- Data for Name: sys_i18n; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--

INSERT INTO basicdb_flowable7.sys_i18n VALUES ('Assembly.buttonPosition', 'Please configure the buttonPosition property for the chart', 'en');
INSERT INTO basicdb_flowable7.sys_i18n VALUES ('Assembly.buttonPosition', '请给图表配置buttonPosition属性', 'zh_CN');
INSERT INTO basicdb_flowable7.sys_i18n VALUES ('Assembly.buttonPosition', '請給圖表配置buttonPosition屬性', 'zh_hant');
INSERT INTO basicdb_flowable7.sys_i18n VALUES ('Assembly.idFormStyle', 'Please configure the idFormStyle property for the chart for more information', 'en');
INSERT INTO basicdb_flowable7.sys_i18n VALUES ('Assembly.idFormStyle', '请给图表配置idFormStyle属性', 'zh_CN');
INSERT INTO basicdb_flowable7.sys_i18n VALUES ('Assembly.idFormStyle', '請給圖表配置idFormStyle屬性', 'zh_hant');


--
-- TOC entry 4885 (class 0 OID 4556477)
-- Dependencies: 516
-- Data for Name: sys_job; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_job VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2023-02-28 17:09:30', '', NULL, '');
INSERT INTO basicdb_flowable7.sys_job VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(''ry'')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2023-02-28 17:09:30', '', NULL, '');
INSERT INTO basicdb_flowable7.sys_job VALUES (3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(''ry'', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2023-02-28 17:09:30', '', NULL, '');


--
-- TOC entry 4887 (class 0 OID 4556487)
-- Dependencies: 518
-- Data for Name: sys_job_log; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4889 (class 0 OID 4556494)
-- Dependencies: 520
-- Data for Name: sys_logininfor; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_logininfor VALUES (725, 'admin', '127.0.0.1', '内网IP', 'Chrome', 'Windows NT', '0', '登录成功', '2026-10-23 16:39:44');
INSERT INTO basicdb_flowable7.sys_logininfor VALUES (726, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '1', '验证码已失效', '2026-10-23 16:54:03');
INSERT INTO basicdb_flowable7.sys_logininfor VALUES (727, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '1', 'Padding error in decryption', '2026-10-23 16:54:15');
INSERT INTO basicdb_flowable7.sys_logininfor VALUES (728, 'admin', '127.0.0.1', '内网IP', 'Chrome', 'Windows NT', '0', '退出成功', '2026-10-23 17:12:30');
INSERT INTO basicdb_flowable7.sys_logininfor VALUES (729, 'admin', '127.0.0.1', '内网IP', 'Chrome', 'Windows NT', '0', '登录成功', '2026-10-23 17:12:33');


--
-- TOC entry 4891 (class 0 OID 4556501)
-- Dependencies: 522
-- Data for Name: sys_menu; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_menu VALUES (3, '系统工具', 0, 3, 'tool', NULL, '', '1', '0', 'M', '0', '0', '', 'tool', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-13 16:16:59', '系统工具目录', NULL, '0', 'system tools', '系統工具');
INSERT INTO basicdb_flowable7.sys_menu VALUES (1, '系统管理', 0, 1, 'system', NULL, '', '1', '0', 'M', '0', '0', '', 'system', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-13 16:17:15', '系统管理目录', NULL, '0', 'system management', '系統管理');
INSERT INTO basicdb_flowable7.sys_menu VALUES (1013, '菜单查询', 102, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1014, '菜单新增', 102, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1015, '菜单修改', 102, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1016, '菜单删除', 102, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1024, '岗位删除', 104, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:post:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1025, '岗位导出', 104, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:post:export', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (108, '日志管理', 1, 9, 'log', '', '', '1', '0', 'M', '0', '0', '', 'log', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:02', '日志管理菜单', NULL, '0', 'log management', '日誌管理');
INSERT INTO basicdb_flowable7.sys_menu VALUES (1045, '日志导出', 501, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1001, '用户查询', 100, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:user:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', 'user query', '用戶查詢');
INSERT INTO basicdb_flowable7.sys_menu VALUES (1002, '用户新增', 100, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:user:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1003, '用户修改', 100, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:user:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1008, '角色查询', 101, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:role:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1009, '角色新增', 101, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:role:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1010, '角色修改', 101, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:role:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1011, '角色删除', 101, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:role:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:55', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1019, '部门修改', 103, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1020, '部门删除', 103, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1030, '字典导出', 105, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1058, '导入代码', 115, 2, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1057, '生成删除', 115, 3, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1059, '预览代码', 115, 4, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1060, '生成代码', 115, 5, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1047, '批量强退', 109, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:05', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1004, '用户删除', 100, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:user:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1005, '用户导出', 100, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:user:export', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1006, '用户导入', 100, 6, '', '', '', '1', '0', 'F', '0', '0', 'system:user:import', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1007, '重置密码', 100, 7, '', '', '', '1', '0', 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1012, '角色导出', 101, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:role:export', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:55', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1017, '部门查询', 103, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:14', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1018, '部门新增', 103, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:14', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1021, '岗位查询', 104, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:post:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1022, '岗位新增', 104, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:post:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1023, '岗位修改', 104, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:post:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1026, '字典查询', 105, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1027, '字典新增', 105, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1028, '字典修改', 105, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1029, '字典删除', 105, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1031, '参数查询', 106, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1032, '参数新增', 106, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:add', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1033, '参数修改', 106, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (100, '用户管理', 1, 1, 'user', 'basic-admin/system/user/index', '', '1', '0', 'C', '0', '0', 'system:user:list', 'user', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '用户管理菜单', NULL, '0', 'user management', '用戶管理');
INSERT INTO basicdb_flowable7.sys_menu VALUES (1034, '参数删除', 106, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1036, '公告查询', 107, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1037, '公告新增', 107, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:add', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1038, '公告修改', 107, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1039, '公告删除', 107, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1040, '操作查询', 500, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:09', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1041, '操作删除', 500, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:09', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1042, '日志导出', 500, 4, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:09', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1043, '登录查询', 501, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1044, '登录删除', 501, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1055, '生成查询', 115, 1, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1056, '生成修改', 115, 2, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1046, '在线查询', 109, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:05', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1049, '任务查询', 110, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:12', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1050, '任务新增', 110, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:12', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1035, '参数导出', 106, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1048, '单条强退', 109, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:05', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1051, '任务修改', 110, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1052, '任务删除', 110, 4, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1053, '状态修改', 110, 5, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (1054, '任务导出', 110, 7, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', '1', '0', 'M', '1', '1', '', 'monitor', 'admin', '2023-02-28 17:09:30', 'admin', '2026-10-16 17:07:09', '系统监控目录', NULL, '0', 'system monitoring', '系統監控');
INSERT INTO basicdb_flowable7.sys_menu VALUES (101, '角色管理', 1, 2, 'role', 'basic-admin/system/role/index', '', '1', '0', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '角色管理菜单', NULL, '0', 'role management', '角色管理');
INSERT INTO basicdb_flowable7.sys_menu VALUES (102, '菜单管理', 1, 3, 'menu', 'basic-admin/system/menu/index', '', '1', '0', 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:36', '菜单管理菜单', NULL, '0', 'menu management', '菜單管理');
INSERT INTO basicdb_flowable7.sys_menu VALUES (103, '部门管理', 1, 4, 'dept', 'basic-admin/system/dept/index', '', '1', '0', 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:14', '部门管理菜单', NULL, '0', 'sector management', '部門管理');
INSERT INTO basicdb_flowable7.sys_menu VALUES (104, '岗位管理', 1, 5, 'post', 'basic-admin/system/post/index', '', '1', '0', 'C', '0', '0', 'system:post:list', 'post', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:35', '岗位管理菜单', NULL, '0', 'post management', '崗位管理');
INSERT INTO basicdb_flowable7.sys_menu VALUES (105, '字典管理', 1, 6, 'dict', 'basic-admin/system/dict/index', '', '1', '0', 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:42', '字典管理菜单', NULL, '0', 'dictionary management', '字典管理');
INSERT INTO basicdb_flowable7.sys_menu VALUES (107, '通知公告', 1, 8, 'notice', 'basic-admin/system/notice/index', '', '1', '0', 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:57', '通知公告菜单', NULL, '0', 'notice announcement', '通知公告');
INSERT INTO basicdb_flowable7.sys_menu VALUES (106, '参数设置', 1, 7, 'config', 'basic-admin/system/config/index', '', '1', '0', 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:51', '参数设置菜单', NULL, '0', 'parameter setting', '參數設置');
INSERT INTO basicdb_flowable7.sys_menu VALUES (500, '操作日志', 108, 1, 'operlog', 'basic-admin/monitor/operlog/index', '', '1', '0', 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:09', '操作日志菜单', NULL, '0', 'operation log', '操作日誌');
INSERT INTO basicdb_flowable7.sys_menu VALUES (501, '登录日志', 108, 2, 'logininfor', 'basic-admin/monitor/logininfor/index', '', '1', '0', 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:15', '登录日志菜单', NULL, '0', 'login logs', '登錄日誌');
INSERT INTO basicdb_flowable7.sys_menu VALUES (111, '数据监控', 3, 4, 'druid', 'basic-admin/monitor/druid/index', '', '1', '0', 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:59', '数据监控菜单', NULL, '0', 'data monitoring', '數據監控');
INSERT INTO basicdb_flowable7.sys_menu VALUES (109, '在线用户', 3, 5, 'online', 'basic-admin/monitor/online/index', '', '1', '0', 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:05', '在线用户菜单', NULL, '0', 'online users', '在線用戶');
INSERT INTO basicdb_flowable7.sys_menu VALUES (112, '服务监控', 3, 7, 'server', 'basic-admin/monitor/server/index', '', '1', '0', 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:21', '服务监控菜单', NULL, '0', 'service monitoring', '服務監控');
INSERT INTO basicdb_flowable7.sys_menu VALUES (113, '缓存监控', 3, 8, 'cache', 'basic-admin/monitor/cache/index', '', '1', '0', 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:27', '缓存监控菜单', NULL, '0', 'Cache monitoring', '緩存監控');
INSERT INTO basicdb_flowable7.sys_menu VALUES (110, '定时任务', 3, 6, 'job', 'basic-admin/monitor/job/index', '', '1', '0', 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:11', '定时任务菜单', NULL, '0', 'scheduled task', '定時任務');
INSERT INTO basicdb_flowable7.sys_menu VALUES (114, '表单构建', 3, 1, 'build', 'basic-admin/tool/build/index', '', '1', '0', 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:36', '表单构建菜单', NULL, '0', 'Form construction', '表單構建');
INSERT INTO basicdb_flowable7.sys_menu VALUES (115, '代码生成', 3, 2, 'gen', 'basic-admin/tool/gen/index', '', '1', '0', 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:44', '代码生成菜单', NULL, '0', 'code generation', '代碼生成');
INSERT INTO basicdb_flowable7.sys_menu VALUES (116, '系统接口', 3, 3, 'swagger', 'basic-admin/tool/swagger/index', '', '1', '0', 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:54', '系统接口菜单', NULL, '0', 'system interface', '系統接口');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2000, '流程引擎', 0, 5, 'workflow', NULL, NULL, '1', '0', 'M', '0', '0', '', 'tree', 'admin', '2026-10-23 16:00:00', NULL, NULL, '工作流目录', NULL, NULL, 'Workflow', '工作流程');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2011, '流程中心', 2000, 0, 'center', 'basic-extend/workflow/center/index', NULL, '1', '0', 'C', '0', '0', 'workflow:process:selectProcessPrcInstance', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '系统上正在流转的流程, 可直接废除', NULL, NULL, 'Process Center', '流程中心');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2001, '我的待办', 2000, 1, 'task/todo', 'basic-extend/workflow/task/todo', NULL, '1', '0', 'C', '0', '0', 'workflow:process:todoList', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '我的待办任务', NULL, NULL, 'Todo', '我的待辦');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2070, '任务审批', 2001, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:complete', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2071, '任务拒绝', 2001, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:reject', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2072, '任务转办', 2001, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:transfer', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2073, '任务委派', 2001, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:delegate', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2074, '任务退回', 2001, 5, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:return', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2075, '任务加签', 2001, 6, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:beforeAddSignTask', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2076, '任务终止', 2001, 7, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:cancel', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2002, '我的已办', 2000, 2, 'task/finished', 'basic-extend/workflow/task/finished', NULL, '1', '0', 'C', '0', '0', 'workflow:process:finishedList', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '我的已办任务', NULL, NULL, 'Finished', '我的已辦');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2003, '我的已发', 2000, 3, 'task/own', 'basic-extend/workflow/task/own', NULL, '1', '0', 'C', '0', '0', 'workflow:process:ownList', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '我发起的流程', NULL, NULL, 'Own', '我的已發');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2077, '流程撤回', 2003, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:revokeProcess', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2004, '抄送我的', 2000, 4, 'task/copy', 'basic-extend/workflow/task/copy', NULL, '1', '0', 'C', '0', '0', 'workflow:process:copyList', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '抄送我的任务', NULL, NULL, 'Copy', '抄送我的');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2005, '待签收', 2000, 5, 'task/claim', 'basic-extend/workflow/task/claim', NULL, '1', '0', 'C', '0', '0', 'workflow:process:claimList', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '待签收任务', NULL, NULL, 'Claim', '待簽收');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2006, '流程分类', 2000, 6, 'category', 'basic-extend/workflow/category/index', NULL, '1', '0', 'C', '0', '0', 'workflow:category:list', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '流程分类管理', NULL, NULL, 'Category', '流程分類');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2030, '分类查询', 2006, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:query', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2031, '分类新增', 2006, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:add', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2032, '分类修改', 2006, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:edit', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2033, '分类删除', 2006, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:remove', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2007, '流程模型', 2000, 7, 'model', 'basic-extend/workflow/model/index', NULL, '1', '0', 'C', '0', '0', 'workflow:model:list', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '流程模型管理', NULL, NULL, 'Model', '流程模型');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2040, '模型查询', 2007, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:list', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2041, '模型新增', 2007, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:add', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2042, '模型修改', 2007, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:edit', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2043, '模型删除', 2007, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:remove', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2044, '模型部署', 2007, 5, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:deploy', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2045, '模型BPMN', 2007, 6, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:bpmnXml', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2046, '模型保存', 2007, 7, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:save', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2047, '模型详情', 2007, 8, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:getInfo', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2012, '表单分类', 2000, 8, 'formCategory', 'basic-extend/workflow/formCategory/index', NULL, '1', '0', 'C', '0', '0', 'workflow:systemCategory:list', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '表单分类管理', NULL, NULL, 'Form Category', '表單分類');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2050, '表单分类查询', 2012, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:systemCategory:list', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2051, '表单分类新增', 2012, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:systemCategory:add', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2052, '表单分类修改', 2012, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:systemCategory:edit', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2053, '表单分类删除', 2012, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:systemCategory:remove', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2009, '表单管理', 2000, 9, 'form', 'basic-extend/workflow/form/index', NULL, '1', '0', 'C', '0', '0', 'workflow:form:list', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '流程表单管理', NULL, NULL, 'Form', '表單管理');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2060, '表单查询', 2009, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:getInfo', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2061, '表单新增', 2009, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:saveForm', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2062, '表单修改', 2009, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:saveForm', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2063, '表单删除', 2009, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:delForm', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2010, '表达式管理', 2000, 10, 'expression', 'basic-extend/workflow/expression/index', NULL, '1', '0', 'C', '0', '0', 'workflow:expression:list', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '表达式字典管理', NULL, NULL, 'Expression', '表達式管理');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2080, '表达式查询', 2010, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:list', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2081, '表达式新增', 2010, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:add', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2082, '表达式修改', 2010, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:edit', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2083, '表达式删除', 2010, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:remove', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2013, '表单模板', 2000, 11, 'formTemplate', 'basic-extend/workflow/formTemplate/index', NULL, '1', '0', 'C', '0', '0', 'workflow:formTemplate:list', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '表单模板管理', NULL, NULL, 'Form Template', '表單模板');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2090, '模板查询', 2013, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:list', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2091, '模板新增', 2013, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:saveForm', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2092, '模板修改', 2013, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:saveForm', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2093, '模板删除', 2013, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:delFormTemplate', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2014, '流程迁移', 2000, 12, 'migrate', 'basic-extend/workflow/migrate/index', NULL, '1', '0', 'C', '0', '0', 'workflow:process:migrate', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '流程实例版本迁移', NULL, NULL, 'Migrate', '流程遷移');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2015, '流程数据清盘', 2000, 13, '#', '', NULL, '1', '0', 'F', '0', '0', 'workflow:process:clearAll', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, '流程数据清盘', NULL, NULL, 'Clear', '流程清盤');
INSERT INTO basicdb_flowable7.sys_menu VALUES (2016, '边界事件触发', 2000, 14, '#', '', NULL, '1', '0', 'F', '0', '0', 'workflow:boundary:trigger', '#', 'admin', '2026-10-23 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_menu VALUES (2017, '测试启动流程', 2000, 20, 'process/start', 'basic-extend/workflow/process/start', NULL, '1', '0', 'C', '0', '0', 'workflow:model:publishModel', 'guide', 'admin', '2026-10-23 16:00:00', NULL, NULL, '快速测试流程发起与流转（仅测试用途）', NULL, NULL, 'Test Start', '測試啟動流程');


--
-- TOC entry 4893 (class 0 OID 4556514)
-- Dependencies: 524
-- Data for Name: sys_notice; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--



--
-- TOC entry 4895 (class 0 OID 4556521)
-- Dependencies: 526
-- Data for Name: sys_notice_read; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4897 (class 0 OID 4556525)
-- Dependencies: 528
-- Data for Name: sys_oper_log; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_oper_log VALUES (379, '操作日志', 9, 'com.basic.web.controller.monitor.SysOperlogController.clean()', 'DELETE', 1, 'admin', NULL, '/monitor/operlog/clean', '127.0.0.1', '内网IP', '{}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 14:17:16');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (380, '登录日志', 9, 'com.basic.web.controller.monitor.SysLogininforController.clean()', 'DELETE', 1, 'admin', NULL, '/monitor/logininfor/clean', '127.0.0.1', '内网IP', '{}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 14:17:20');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (381, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1754778882690195458', '127.0.0.1', '内网IP', '{menuId=1754778882690195458}', '{"msg":"存在子菜单,不允许删除","code":500}', 0, NULL, '2026-10-14 14:17:33');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (382, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1064', '127.0.0.1', '内网IP', '{menuId=1064}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 14:17:41');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (383, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1955896250820567041', '127.0.0.1', '内网IP', '{menuId=1955896250820567041}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:06:47');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (384, '菜单管理', 2, 'com.basic.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":null,"createTime":"2023-02-28 17:09:30","updateBy":"admin","updateTime":null,"remark":null,"params":{},"menuId":2,"menuName":"系统监控","parentName":null,"parentId":0,"orderNum":2,"path":"monitor","component":null,"query":"","isFrame":"1","isCache":"0","menuType":"M","visible":"0","status":"0","perms":"","icon":"monitor","children":[]}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:06:56');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (385, '菜单管理', 2, 'com.basic.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":null,"createTime":"2023-02-28 17:09:30","updateBy":"admin","updateTime":null,"remark":null,"params":{},"menuId":2,"menuName":"系统监控","parentName":null,"parentId":0,"orderNum":2,"path":"monitor","component":null,"query":"","isFrame":"1","isCache":"0","menuType":"M","visible":"1","status":"1","perms":"","icon":"monitor","children":[]}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:09');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (386, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753950971045343233', '127.0.0.1', '内网IP', '{menuId=1753950971045343233}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:24');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (387, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796354', '127.0.0.1', '内网IP', '{menuId=1753952621629796354}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:26');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (388, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796355', '127.0.0.1', '内网IP', '{menuId=1753952621629796355}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:30');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (389, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796356', '127.0.0.1', '内网IP', '{menuId=1753952621629796356}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:32');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (390, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796357', '127.0.0.1', '内网IP', '{menuId=1753952621629796357}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:34');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (391, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796358', '127.0.0.1', '内网IP', '{menuId=1753952621629796358}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:37');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (392, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1061', '127.0.0.1', '内网IP', '{menuId=1061}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:39');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (393, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1754778882690195458', '127.0.0.1', '内网IP', '{menuId=1754778882690195458}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:42');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (394, '自定义单分类', 1, 'com.basic.workflow.controller.WfFormCategoryController.add()', 'POST', 1, 'admin', NULL, '/system/category', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":"admin","createTime":"2026-10-23 17:05:21","updateBy":null,"updateTime":null,"remark":null,"params":{},"id":"40ba05800454d652fce8c7a733d40de6","categoryName":"测试","parent":"0","children":null}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-23 17:05:22');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (395, '自定义单分类', 1, 'com.basic.workflow.controller.WfFormCategoryController.add()', 'POST', 1, 'admin', NULL, '/system/category', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":"admin","createTime":"2026-10-23 17:05:28","updateBy":null,"updateTime":null,"remark":null,"params":{},"id":"d45c931c294f1a930775d10ce9a00804","categoryName":"村上春树","parent":"40ba05800454d652fce8c7a733d40de6","children":null}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-23 17:05:29');
INSERT INTO basicdb_flowable7.sys_oper_log VALUES (396, '菜单管理', 2, 'com.basic.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":null,"createTime":"2026-10-23 16:00:00","updateBy":"admin","updateTime":null,"remark":null,"params":{},"menuId":2017,"menuName":"测试启动流程","parentName":null,"parentId":2000,"orderNum":20,"path":"process/start","component":"basic-extend/workflow/process/start","query":null,"isFrame":"1","isCache":"0","menuType":"C","visible":"0","status":"0","perms":"workflow:model:publishModel","icon":"guide","children":[]}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-23 17:12:24');


--
-- TOC entry 4899 (class 0 OID 4556534)
-- Dependencies: 530
-- Data for Name: sys_post; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_post VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2023-02-28 17:09:30', '', NULL, '');


--
-- TOC entry 4901 (class 0 OID 4556540)
-- Dependencies: 532
-- Data for Name: sys_role; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_role VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '超级管理员');
INSERT INTO basicdb_flowable7.sys_role VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-01-24 16:28:19', '普通角色');
INSERT INTO basicdb_flowable7.sys_role VALUES (4, '333', 'hgf', 0, '1', 1, 1, '0', '2', 'admin', '2024-02-06 14:00:18', NULL, NULL, NULL);
INSERT INTO basicdb_flowable7.sys_role VALUES (5, '用户1', 'usss', 3, '1', 1, 1, '0', '2', 'admin', '2024-02-06 16:52:25', NULL, NULL, NULL);


--
-- TOC entry 4902 (class 0 OID 4556549)
-- Dependencies: 533
-- Data for Name: sys_role_dept; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_role_dept VALUES (2, 100);
INSERT INTO basicdb_flowable7.sys_role_dept VALUES (2, 101);
INSERT INTO basicdb_flowable7.sys_role_dept VALUES (2, 105);


--
-- TOC entry 4903 (class 0 OID 4556552)
-- Dependencies: 534
-- Data for Name: sys_role_menu; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 105);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 100);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1001);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1002);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1003);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1004);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1005);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1006);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1007);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 101);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1008);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1009);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1010);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1011);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1012);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 102);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1013);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1014);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1015);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1016);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 103);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1017);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1018);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1019);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1020);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 104);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1021);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1022);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1023);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1024);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1025);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 1026);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 106);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 107);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 108);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 500);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 501);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 2);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 109);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 110);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 111);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 112);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 113);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 3);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 114);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 115);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (2, 116);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2000);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2011);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2001);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2070);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2071);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2072);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2073);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2074);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2075);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2076);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2002);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2003);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2077);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2004);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2005);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2006);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2030);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2031);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2032);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2033);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2007);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2040);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2041);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2042);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2043);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2044);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2045);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2046);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2047);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2012);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2050);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2051);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2052);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2053);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2009);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2060);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2061);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2062);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2063);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2010);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2080);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2081);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2082);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2083);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2013);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2090);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2091);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2092);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2093);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2014);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2015);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2016);
INSERT INTO basicdb_flowable7.sys_role_menu VALUES (1, 2017);


--
-- TOC entry 4905 (class 0 OID 4556556)
-- Dependencies: 536
-- Data for Name: sys_user; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_user VALUES (9, NULL, 'test002', '测试2', '00', NULL, NULL, '0', NULL, '$2a$10$udfBxRm0omFl7XpSmBk7/.wfsRfzP5qqc0EJ6bM0s01fgmoSiDIBq', '0', '0', '127.0.0.1', '2024-02-21 17:18:01', 'admin', '2024-02-21 17:17:44', NULL, '2024-02-21 17:18:00', NULL, NULL);
INSERT INTO basicdb_flowable7.sys_user VALUES (4, 101, 'xiaoan', '安全管理员', '00', 'xiaoan@email.com', '19999999999', '0', '/profile/avatar/2024/04/22/blob_20240422160106A001.jpeg', '$2a$10$So1TNdFpzE1dB09Z0eQs1OzFpFLMrWxbYtfTvxEFLMC/8SXTUPPEK', '0', '0', '127.0.0.1', '2024-10-11 10:13:43', 'admin', '2024-01-26 15:07:37', 'admin', '2024-10-11 10:13:42', '安全管理员', '2024-10-11 10:13:27');
INSERT INTO basicdb_flowable7.sys_user VALUES (6, NULL, '13249230230', 'AA', '00', NULL, NULL, '0', NULL, '$2a$10$.3MLA6zZE6RPDLzSx6jzseXhe9ShBXBgddJ5Z09Bp1immgkZ978JS', '0', '0', '192.168.170.21', '2024-02-06 15:51:51', 'admin', '2024-01-26 15:24:02', 'admin', '2024-02-06 15:51:50', NULL, '2024-02-21 11:49:28');
INSERT INTO basicdb_flowable7.sys_user VALUES (5, 100, 'xiaoxi', '系统管理员', '00', 'xiaoxi@email.com', '13333333333', '0', NULL, '$2a$10$FNM2ZPI/XwzY.arKiLUZtusSs9ui1X0xU3dfdcvLr.mb9eNskz22O', '0', '0', '192.168.170.21', '2024-04-24 09:12:57', 'admin', '2024-01-26 15:10:01', 'admin', '2024-04-24 09:12:56', '系统管理员', '2024-04-22 16:03:29');
INSERT INTO basicdb_flowable7.sys_user VALUES (7, NULL, '用户1', 'user1', '00', NULL, NULL, '0', NULL, '$2a$10$wfJ4zAYQ0kaZyp2XRyLO7.5bnloK3/M0HiOatf3kRWt//u2KMUNdu', '0', '2', NULL, NULL, 'admin', '2024-02-06 16:51:53', NULL, NULL, NULL, '2024-02-21 11:49:28');
INSERT INTO basicdb_flowable7.sys_user VALUES (2, 105, 'xiaoshen', '审计管理员', '00', 'xiaoshen@qq.com', '17777777777', '1', '/profile/avatar/2024/02/06/blob_20240206154427A003.jpeg', '$2a$10$MpBkNRsOzwiy54C/Vx8JoOjk6NV3Zz.jws030m1DMvguCcPP4OLJ6', '0', '0', '192.168.170.21', '2024-02-07 08:21:20', 'admin', '2023-02-28 17:09:30', 'admin', '2024-02-07 08:21:19', '审计管理员', '2024-02-21 11:49:28');
INSERT INTO basicdb_flowable7.sys_user VALUES (8, NULL, 'test001', '测试', '00', NULL, NULL, '0', NULL, '$2a$10$RAs8Dzmmt.UUD4JZvLX4Wes1Eblh0Z/OpQceqseQk78zhNvI62XvK', '0', '0', '127.0.0.1', '2025-08-14 10:43:36', 'admin', '2024-02-21 17:09:51', 'admin', '2025-08-14 10:43:36', NULL, '2025-08-14 10:43:21');
INSERT INTO basicdb_flowable7.sys_user VALUES (1, 100, 'admin', '超*管', '00', 'xiaochao@163.com', '18888888888', '1', '/profile/avatar/2024/02/06/blob_20240206154306A002.jpeg', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2026-10-23 17:12:33', 'admin', '2023-02-28 17:09:30', '', '2026-10-23 17:12:33', '管理员', '2024-02-21 11:49:28');


--
-- TOC entry 4906 (class 0 OID 4556565)
-- Dependencies: 537
-- Data for Name: sys_user_post; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_user_post VALUES (1, 1);


--
-- TOC entry 4907 (class 0 OID 4556568)
-- Dependencies: 538
-- Data for Name: sys_user_role; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: postgres
--

INSERT INTO basicdb_flowable7.sys_user_role VALUES (1, 1);
INSERT INTO basicdb_flowable7.sys_user_role VALUES (4, 2);
INSERT INTO basicdb_flowable7.sys_user_role VALUES (2, 2);
INSERT INTO basicdb_flowable7.sys_user_role VALUES (5, 2);
INSERT INTO basicdb_flowable7.sys_user_role VALUES (8, 2);
INSERT INTO basicdb_flowable7.sys_user_role VALUES (9, 2);


--
-- TOC entry 4956 (class 0 OID 4557560)
-- Dependencies: 587
-- Data for Name: tbl_flow_extend_procinst; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4947 (class 0 OID 4557476)
-- Dependencies: 578
-- Data for Name: wf_category; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4955 (class 0 OID 4557550)
-- Dependencies: 586
-- Data for Name: wf_comment_info; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4954 (class 0 OID 4557540)
-- Dependencies: 585
-- Data for Name: wf_copy; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4952 (class 0 OID 4557521)
-- Dependencies: 583
-- Data for Name: wf_deploy_form; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4949 (class 0 OID 4557495)
-- Dependencies: 580
-- Data for Name: wf_form; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4950 (class 0 OID 4557504)
-- Dependencies: 581
-- Data for Name: wf_form_category; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4951 (class 0 OID 4557513)
-- Dependencies: 582
-- Data for Name: wf_form_template; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 4953 (class 0 OID 4557530)
-- Dependencies: 584
-- Data for Name: wf_task_form; Type: TABLE DATA; Schema: basicdb_flowable7; Owner: system
--



--
-- TOC entry 5121 (class 0 OID 0)
-- Dependencies: 565
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: system
--

SELECT pg_catalog.setval('basicdb_flowable7.act_evt_log_log_nr__seq', 1, false);


--
-- TOC entry 5122 (class 0 OID 0)
-- Dependencies: 556
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: system
--

SELECT pg_catalog.setval('basicdb_flowable7.act_hi_tsk_log_id__seq', 1, false);


--
-- TOC entry 5123 (class 0 OID 0)
-- Dependencies: 490
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.gen_table_column_column_id_seq', 185, true);


--
-- TOC entry 5124 (class 0 OID 0)
-- Dependencies: 491
-- Name: gen_table_table_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: system
--

SELECT pg_catalog.setval('basicdb_flowable7.gen_table_table_id_seq', 18, true);


--
-- TOC entry 5125 (class 0 OID 0)
-- Dependencies: 506
-- Name: sys_config_config_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_config_config_id_seq', 13, true);


--
-- TOC entry 5126 (class 0 OID 0)
-- Dependencies: 509
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_dept_dept_id_seq', 111, true);


--
-- TOC entry 5127 (class 0 OID 0)
-- Dependencies: 511
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_dict_data_dict_code_seq', 32, true);


--
-- TOC entry 5128 (class 0 OID 0)
-- Dependencies: 513
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_dict_type_dict_id_seq', 12, true);


--
-- TOC entry 5129 (class 0 OID 0)
-- Dependencies: 517
-- Name: sys_job_job_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_job_job_id_seq', 4, true);


--
-- TOC entry 5130 (class 0 OID 0)
-- Dependencies: 519
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_job_log_job_log_id_seq', 1, false);


--
-- TOC entry 5131 (class 0 OID 0)
-- Dependencies: 521
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_logininfor_info_id_seq', 729, true);


--
-- TOC entry 5132 (class 0 OID 0)
-- Dependencies: 523
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_menu_menu_id_seq', 1066, true);


--
-- TOC entry 5133 (class 0 OID 0)
-- Dependencies: 525
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_notice_notice_id_seq', 3, true);


--
-- TOC entry 5134 (class 0 OID 0)
-- Dependencies: 527
-- Name: sys_notice_read_read_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: system
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_notice_read_read_id_seq', 1, false);


--
-- TOC entry 5135 (class 0 OID 0)
-- Dependencies: 529
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_oper_log_oper_id_seq', 396, true);


--
-- TOC entry 5136 (class 0 OID 0)
-- Dependencies: 531
-- Name: sys_post_post_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_post_post_id_seq', 5, true);


--
-- TOC entry 5137 (class 0 OID 0)
-- Dependencies: 535
-- Name: sys_role_role_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_role_role_id_seq', 5, true);


--
-- TOC entry 5138 (class 0 OID 0)
-- Dependencies: 539
-- Name: sys_user_user_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable7; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable7.sys_user_user_id_seq', 9, true);


--
-- TOC entry 4627 (class 2606 OID 4557378)
-- Name: flw_channel_definition FLW_CHANNEL_DEFINITION_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.flw_channel_definition
    ADD CONSTRAINT "FLW_CHANNEL_DEFINITION_pkey" PRIMARY KEY (id_);


--
-- TOC entry 4624 (class 2606 OID 4557364)
-- Name: flw_event_definition FLW_EVENT_DEFINITION_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.flw_event_definition
    ADD CONSTRAINT "FLW_EVENT_DEFINITION_pkey" PRIMARY KEY (id_);


--
-- TOC entry 4619 (class 2606 OID 4557350)
-- Name: flw_event_deployment FLW_EVENT_DEPLOYMENT_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.flw_event_deployment
    ADD CONSTRAINT "FLW_EVENT_DEPLOYMENT_pkey" PRIMARY KEY (id_);


--
-- TOC entry 4621 (class 2606 OID 4557357)
-- Name: flw_event_resource FLW_EVENT_RESOURCE_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.flw_event_resource
    ADD CONSTRAINT "FLW_EVENT_RESOURCE_pkey" PRIMARY KEY (id_);


--
-- TOC entry 4354 (class 2606 OID 4556588)
-- Name: gen_table PRIMARY; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.gen_table
    ADD CONSTRAINT "PRIMARY" PRIMARY KEY (table_id);


--
-- TOC entry 4356 (class 2606 OID 4556590)
-- Name: gen_table_column PRIMARY00000; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.gen_table_column
    ADD CONSTRAINT "PRIMARY00000" PRIMARY KEY (column_id);


--
-- TOC entry 4358 (class 2606 OID 4556592)
-- Name: qrtz_blob_triggers PRIMARY00001; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.qrtz_blob_triggers
    ADD CONSTRAINT "PRIMARY00001" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4360 (class 2606 OID 4556594)
-- Name: qrtz_calendars PRIMARY00002; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.qrtz_calendars
    ADD CONSTRAINT "PRIMARY00002" PRIMARY KEY (sched_name, calendar_name);


--
-- TOC entry 4362 (class 2606 OID 4556596)
-- Name: qrtz_cron_triggers PRIMARY00003; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.qrtz_cron_triggers
    ADD CONSTRAINT "PRIMARY00003" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4364 (class 2606 OID 4556598)
-- Name: qrtz_fired_triggers PRIMARY00004; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.qrtz_fired_triggers
    ADD CONSTRAINT "PRIMARY00004" PRIMARY KEY (sched_name, entry_id);


--
-- TOC entry 4366 (class 2606 OID 4556600)
-- Name: qrtz_job_details PRIMARY00005; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.qrtz_job_details
    ADD CONSTRAINT "PRIMARY00005" PRIMARY KEY (sched_name, job_name, job_group);


--
-- TOC entry 4368 (class 2606 OID 4556602)
-- Name: qrtz_locks PRIMARY00006; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.qrtz_locks
    ADD CONSTRAINT "PRIMARY00006" PRIMARY KEY (sched_name, lock_name);


--
-- TOC entry 4370 (class 2606 OID 4556604)
-- Name: qrtz_paused_trigger_grps PRIMARY00007; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.qrtz_paused_trigger_grps
    ADD CONSTRAINT "PRIMARY00007" PRIMARY KEY (sched_name, trigger_group);


--
-- TOC entry 4372 (class 2606 OID 4556606)
-- Name: qrtz_scheduler_state PRIMARY00008; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.qrtz_scheduler_state
    ADD CONSTRAINT "PRIMARY00008" PRIMARY KEY (sched_name, instance_name);


--
-- TOC entry 4374 (class 2606 OID 4556608)
-- Name: qrtz_simple_triggers PRIMARY00009; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.qrtz_simple_triggers
    ADD CONSTRAINT "PRIMARY00009" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4376 (class 2606 OID 4556610)
-- Name: qrtz_simprop_triggers PRIMARY00010; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.qrtz_simprop_triggers
    ADD CONSTRAINT "PRIMARY00010" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4378 (class 2606 OID 4556612)
-- Name: qrtz_triggers PRIMARY00011; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.qrtz_triggers
    ADD CONSTRAINT "PRIMARY00011" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4381 (class 2606 OID 4556614)
-- Name: sys_config PRIMARY00012; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_config
    ADD CONSTRAINT "PRIMARY00012" PRIMARY KEY (config_id);


--
-- TOC entry 4385 (class 2606 OID 4556616)
-- Name: sys_dept PRIMARY00013; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_dept
    ADD CONSTRAINT "PRIMARY00013" PRIMARY KEY (dept_id);


--
-- TOC entry 4387 (class 2606 OID 4556618)
-- Name: sys_dict_data PRIMARY00014; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_dict_data
    ADD CONSTRAINT "PRIMARY00014" PRIMARY KEY (dict_code);


--
-- TOC entry 4389 (class 2606 OID 4556620)
-- Name: sys_dict_type PRIMARY00015; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_dict_type
    ADD CONSTRAINT "PRIMARY00015" PRIMARY KEY (dict_id);


--
-- TOC entry 4394 (class 2606 OID 4556622)
-- Name: sys_job PRIMARY00016; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_job
    ADD CONSTRAINT "PRIMARY00016" PRIMARY KEY (job_id, job_name, job_group);


--
-- TOC entry 4396 (class 2606 OID 4556624)
-- Name: sys_job_log PRIMARY00017; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_job_log
    ADD CONSTRAINT "PRIMARY00017" PRIMARY KEY (job_log_id);


--
-- TOC entry 4398 (class 2606 OID 4556626)
-- Name: sys_logininfor PRIMARY00018; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_logininfor
    ADD CONSTRAINT "PRIMARY00018" PRIMARY KEY (info_id);


--
-- TOC entry 4400 (class 2606 OID 4556628)
-- Name: sys_menu PRIMARY00019; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_menu
    ADD CONSTRAINT "PRIMARY00019" PRIMARY KEY (menu_id);


--
-- TOC entry 4402 (class 2606 OID 4556630)
-- Name: sys_notice PRIMARY00020; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_notice
    ADD CONSTRAINT "PRIMARY00020" PRIMARY KEY (notice_id);


--
-- TOC entry 4407 (class 2606 OID 4556632)
-- Name: sys_oper_log PRIMARY00021; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_oper_log
    ADD CONSTRAINT "PRIMARY00021" PRIMARY KEY (oper_id);


--
-- TOC entry 4409 (class 2606 OID 4556634)
-- Name: sys_post PRIMARY00022; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_post
    ADD CONSTRAINT "PRIMARY00022" PRIMARY KEY (post_id);


--
-- TOC entry 4411 (class 2606 OID 4556636)
-- Name: sys_role PRIMARY00023; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_role
    ADD CONSTRAINT "PRIMARY00023" PRIMARY KEY (role_id);


--
-- TOC entry 4413 (class 2606 OID 4556638)
-- Name: sys_role_dept PRIMARY00024; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_role_dept
    ADD CONSTRAINT "PRIMARY00024" PRIMARY KEY (role_id, dept_id);


--
-- TOC entry 4415 (class 2606 OID 4556640)
-- Name: sys_role_menu PRIMARY00025; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_role_menu
    ADD CONSTRAINT "PRIMARY00025" PRIMARY KEY (role_id, menu_id);


--
-- TOC entry 4417 (class 2606 OID 4556642)
-- Name: sys_user PRIMARY00026; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_user
    ADD CONSTRAINT "PRIMARY00026" PRIMARY KEY (user_id);


--
-- TOC entry 4419 (class 2606 OID 4556644)
-- Name: sys_user_post PRIMARY00027; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_user_post
    ADD CONSTRAINT "PRIMARY00027" PRIMARY KEY (user_id, post_id);


--
-- TOC entry 4421 (class 2606 OID 4556646)
-- Name: sys_user_role PRIMARY00028; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable7.sys_user_role
    ADD CONSTRAINT "PRIMARY00028" PRIMARY KEY (user_id, role_id);


--
-- TOC entry 4578 (class 2606 OID 4557070)
-- Name: act_evt_log act_evt_log_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_evt_log
    ADD CONSTRAINT act_evt_log_pkey PRIMARY KEY (log_nr_);


--
-- TOC entry 4425 (class 2606 OID 4556758)
-- Name: act_ge_bytearray act_ge_bytearray_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ge_bytearray
    ADD CONSTRAINT act_ge_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 4423 (class 2606 OID 4556751)
-- Name: act_ge_property act_ge_property_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ge_property
    ADD CONSTRAINT act_ge_property_pkey PRIMARY KEY (name_);


--
-- TOC entry 4602 (class 2606 OID 4557304)
-- Name: act_hi_actinst act_hi_actinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_hi_actinst
    ADD CONSTRAINT act_hi_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4617 (class 2606 OID 4557325)
-- Name: act_hi_attachment act_hi_attachment_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_hi_attachment
    ADD CONSTRAINT act_hi_attachment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4615 (class 2606 OID 4557318)
-- Name: act_hi_comment act_hi_comment_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_hi_comment
    ADD CONSTRAINT act_hi_comment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4608 (class 2606 OID 4557311)
-- Name: act_hi_detail act_hi_detail_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_hi_detail
    ADD CONSTRAINT act_hi_detail_pkey PRIMARY KEY (id_);


--
-- TOC entry 4434 (class 2606 OID 4556776)
-- Name: act_hi_entitylink act_hi_entitylink_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_hi_entitylink
    ADD CONSTRAINT act_hi_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4450 (class 2606 OID 4556799)
-- Name: act_hi_identitylink act_hi_identitylink_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_hi_identitylink
    ADD CONSTRAINT act_hi_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4595 (class 2606 OID 4557293)
-- Name: act_hi_procinst act_hi_procinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4597 (class 2606 OID 4557295)
-- Name: act_hi_procinst act_hi_procinst_proc_inst_id__key; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_proc_inst_id__key UNIQUE (proc_inst_id_);


--
-- TOC entry 4527 (class 2606 OID 4556975)
-- Name: act_hi_taskinst act_hi_taskinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_hi_taskinst
    ADD CONSTRAINT act_hi_taskinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4533 (class 2606 OID 4556985)
-- Name: act_hi_tsk_log act_hi_tsk_log_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_hi_tsk_log
    ADD CONSTRAINT act_hi_tsk_log_pkey PRIMARY KEY (id_);


--
-- TOC entry 4544 (class 2606 OID 4557012)
-- Name: act_hi_varinst act_hi_varinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_hi_varinst
    ADD CONSTRAINT act_hi_varinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4582 (class 2606 OID 4557075)
-- Name: act_procdef_info act_procdef_info_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_procdef_info
    ADD CONSTRAINT act_procdef_info_pkey PRIMARY KEY (id_);


--
-- TOC entry 4558 (class 2606 OID 4557035)
-- Name: act_re_deployment act_re_deployment_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_re_deployment
    ADD CONSTRAINT act_re_deployment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4563 (class 2606 OID 4557043)
-- Name: act_re_model act_re_model_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_re_model
    ADD CONSTRAINT act_re_model_pkey PRIMARY KEY (id_);


--
-- TOC entry 4574 (class 2606 OID 4557060)
-- Name: act_re_procdef act_re_procdef_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_re_procdef
    ADD CONSTRAINT act_re_procdef_pkey PRIMARY KEY (id_);


--
-- TOC entry 4593 (class 2606 OID 4557084)
-- Name: act_ru_actinst act_ru_actinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_actinst
    ADD CONSTRAINT act_ru_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4501 (class 2606 OID 4556835)
-- Name: act_ru_deadletter_job act_ru_deadletter_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_deadletter_job
    ADD CONSTRAINT act_ru_deadletter_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4432 (class 2606 OID 4556765)
-- Name: act_ru_entitylink act_ru_entitylink_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_entitylink
    ADD CONSTRAINT act_ru_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4556 (class 2606 OID 4557023)
-- Name: act_ru_event_subscr act_ru_event_subscr_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_event_subscr
    ADD CONSTRAINT act_ru_event_subscr_pkey PRIMARY KEY (id_);


--
-- TOC entry 4572 (class 2606 OID 4557051)
-- Name: act_ru_execution act_ru_execution_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_execution
    ADD CONSTRAINT act_ru_execution_pkey PRIMARY KEY (id_);


--
-- TOC entry 4511 (class 2606 OID 4556851)
-- Name: act_ru_external_job act_ru_external_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_external_job
    ADD CONSTRAINT act_ru_external_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4503 (class 2606 OID 4556843)
-- Name: act_ru_history_job act_ru_history_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_history_job
    ADD CONSTRAINT act_ru_history_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4448 (class 2606 OID 4556787)
-- Name: act_ru_identitylink act_ru_identitylink_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_identitylink
    ADD CONSTRAINT act_ru_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4467 (class 2606 OID 4556811)
-- Name: act_ru_job act_ru_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_job
    ADD CONSTRAINT act_ru_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4490 (class 2606 OID 4556827)
-- Name: act_ru_suspended_job act_ru_suspended_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_suspended_job
    ADD CONSTRAINT act_ru_suspended_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4525 (class 2606 OID 4556962)
-- Name: act_ru_task act_ru_task_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_task
    ADD CONSTRAINT act_ru_task_pkey PRIMARY KEY (id_);


--
-- TOC entry 4479 (class 2606 OID 4556819)
-- Name: act_ru_timer_job act_ru_timer_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_timer_job
    ADD CONSTRAINT act_ru_timer_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4542 (class 2606 OID 4556996)
-- Name: act_ru_variable act_ru_variable_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_variable
    ADD CONSTRAINT act_ru_variable_pkey PRIMARY KEY (id_);


--
-- TOC entry 4584 (class 2606 OID 4557284)
-- Name: act_procdef_info act_uniq_info_procdef; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_procdef_info
    ADD CONSTRAINT act_uniq_info_procdef UNIQUE (proc_def_id_);


--
-- TOC entry 4576 (class 2606 OID 4557103)
-- Name: act_re_procdef act_uniq_procdef; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_re_procdef
    ADD CONSTRAINT act_uniq_procdef UNIQUE (key_, version_, derived_version_, tenant_id_);


--
-- TOC entry 4634 (class 2606 OID 4557494)
-- Name: flw_expression flw_expression_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.flw_expression
    ADD CONSTRAINT flw_expression_pkey PRIMARY KEY (id);


--
-- TOC entry 4516 (class 2606 OID 4556948)
-- Name: flw_ru_batch_part flw_ru_batch_part_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.flw_ru_batch_part
    ADD CONSTRAINT flw_ru_batch_part_pkey PRIMARY KEY (id_);


--
-- TOC entry 4513 (class 2606 OID 4556940)
-- Name: flw_ru_batch flw_ru_batch_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.flw_ru_batch
    ADD CONSTRAINT flw_ru_batch_pkey PRIMARY KEY (id_);


--
-- TOC entry 4383 (class 2606 OID 4556648)
-- Name: sys_demo sys_demo_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.sys_demo
    ADD CONSTRAINT sys_demo_pkey PRIMARY KEY (id);


--
-- TOC entry 4392 (class 2606 OID 4556650)
-- Name: sys_files sys_files_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.sys_files
    ADD CONSTRAINT sys_files_pkey PRIMARY KEY (id);


--
-- TOC entry 4404 (class 2606 OID 4556652)
-- Name: sys_notice_read sys_notice_read_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.sys_notice_read
    ADD CONSTRAINT sys_notice_read_pkey PRIMARY KEY (read_id);


--
-- TOC entry 4664 (class 2606 OID 4557566)
-- Name: tbl_flow_extend_procinst tbl_flow_extend_procinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.tbl_flow_extend_procinst
    ADD CONSTRAINT tbl_flow_extend_procinst_pkey PRIMARY KEY (id);


--
-- TOC entry 4632 (class 2606 OID 4557484)
-- Name: wf_category wf_category_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.wf_category
    ADD CONSTRAINT wf_category_pkey PRIMARY KEY (category_id);


--
-- TOC entry 4660 (class 2606 OID 4557557)
-- Name: wf_comment_info wf_comment_info_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.wf_comment_info
    ADD CONSTRAINT wf_comment_info_pkey PRIMARY KEY (id);


--
-- TOC entry 4656 (class 2606 OID 4557547)
-- Name: wf_copy wf_copy_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.wf_copy
    ADD CONSTRAINT wf_copy_pkey PRIMARY KEY (copy_id);


--
-- TOC entry 4647 (class 2606 OID 4557527)
-- Name: wf_deploy_form wf_deploy_form_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.wf_deploy_form
    ADD CONSTRAINT wf_deploy_form_pkey PRIMARY KEY (id);


--
-- TOC entry 4641 (class 2606 OID 4557511)
-- Name: wf_form_category wf_form_category_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.wf_form_category
    ADD CONSTRAINT wf_form_category_pkey PRIMARY KEY (id);


--
-- TOC entry 4638 (class 2606 OID 4557501)
-- Name: wf_form wf_form_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.wf_form
    ADD CONSTRAINT wf_form_pkey PRIMARY KEY (form_id);


--
-- TOC entry 4643 (class 2606 OID 4557520)
-- Name: wf_form_template wf_form_template_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.wf_form_template
    ADD CONSTRAINT wf_form_template_pkey PRIMARY KEY (form_id);


--
-- TOC entry 4652 (class 2606 OID 4557536)
-- Name: wf_task_form wf_task_form_pkey; Type: CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.wf_task_form
    ADD CONSTRAINT wf_task_form_pkey PRIMARY KEY (id);


--
-- TOC entry 4534 (class 1259 OID 4556989)
-- Name: act_idx_act_hi_tsk_log_task; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_act_hi_tsk_log_task ON basicdb_flowable7.act_hi_tsk_log USING btree (task_id_);


--
-- TOC entry 4439 (class 1259 OID 4557134)
-- Name: act_idx_athrz_procedef; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_athrz_procedef ON basicdb_flowable7.act_ru_identitylink USING btree (proc_def_id_);


--
-- TOC entry 4426 (class 1259 OID 4557089)
-- Name: act_idx_bytear_depl; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_bytear_depl ON basicdb_flowable7.act_ge_bytearray USING btree (deployment_id_);


--
-- TOC entry 4628 (class 1259 OID 4557379)
-- Name: act_idx_channel_def_uniq; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE UNIQUE INDEX act_idx_channel_def_uniq ON basicdb_flowable7.flw_channel_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 4491 (class 1259 OID 4556864)
-- Name: act_idx_deadletter_job_correlation_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_deadletter_job_correlation_id ON basicdb_flowable7.act_ru_deadletter_job USING btree (correlation_id_);


--
-- TOC entry 4492 (class 1259 OID 4556863)
-- Name: act_idx_deadletter_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_deadletter_job_custom_values_id ON basicdb_flowable7.act_ru_deadletter_job USING btree (custom_values_id_);


--
-- TOC entry 4493 (class 1259 OID 4556862)
-- Name: act_idx_deadletter_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_deadletter_job_exception_stack_id ON basicdb_flowable7.act_ru_deadletter_job USING btree (exception_stack_id_);


--
-- TOC entry 4494 (class 1259 OID 4557230)
-- Name: act_idx_deadletter_job_execution_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_deadletter_job_execution_id ON basicdb_flowable7.act_ru_deadletter_job USING btree (execution_id_);


--
-- TOC entry 4495 (class 1259 OID 4557242)
-- Name: act_idx_deadletter_job_proc_def_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_deadletter_job_proc_def_id ON basicdb_flowable7.act_ru_deadletter_job USING btree (proc_def_id_);


--
-- TOC entry 4496 (class 1259 OID 4557236)
-- Name: act_idx_deadletter_job_process_instance_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_deadletter_job_process_instance_id ON basicdb_flowable7.act_ru_deadletter_job USING btree (process_instance_id_);


--
-- TOC entry 4497 (class 1259 OID 4556927)
-- Name: act_idx_djob_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_djob_scope ON basicdb_flowable7.act_ru_deadletter_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4498 (class 1259 OID 4556929)
-- Name: act_idx_djob_scope_def; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_djob_scope_def ON basicdb_flowable7.act_ru_deadletter_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4499 (class 1259 OID 4556928)
-- Name: act_idx_djob_sub_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_djob_sub_scope ON basicdb_flowable7.act_ru_deadletter_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4504 (class 1259 OID 4556930)
-- Name: act_idx_ejob_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ejob_scope ON basicdb_flowable7.act_ru_external_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4505 (class 1259 OID 4556932)
-- Name: act_idx_ejob_scope_def; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ejob_scope_def ON basicdb_flowable7.act_ru_external_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4506 (class 1259 OID 4556931)
-- Name: act_idx_ejob_sub_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ejob_sub_scope ON basicdb_flowable7.act_ru_external_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4427 (class 1259 OID 4556767)
-- Name: act_idx_ent_lnk_ref_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ent_lnk_ref_scope ON basicdb_flowable7.act_ru_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 4428 (class 1259 OID 4556768)
-- Name: act_idx_ent_lnk_root_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ent_lnk_root_scope ON basicdb_flowable7.act_ru_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 4429 (class 1259 OID 4556766)
-- Name: act_idx_ent_lnk_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ent_lnk_scope ON basicdb_flowable7.act_ru_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 4430 (class 1259 OID 4556769)
-- Name: act_idx_ent_lnk_scope_def; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ent_lnk_scope_def ON basicdb_flowable7.act_ru_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 4625 (class 1259 OID 4557371)
-- Name: act_idx_event_def_uniq; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE UNIQUE INDEX act_idx_event_def_uniq ON basicdb_flowable7.flw_event_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 4551 (class 1259 OID 4557025)
-- Name: act_idx_event_subscr; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_event_subscr ON basicdb_flowable7.act_ru_event_subscr USING btree (execution_id_);


--
-- TOC entry 4552 (class 1259 OID 4557024)
-- Name: act_idx_event_subscr_config_; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_event_subscr_config_ ON basicdb_flowable7.act_ru_event_subscr USING btree (configuration_);


--
-- TOC entry 4553 (class 1259 OID 4557026)
-- Name: act_idx_event_subscr_proc_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_event_subscr_proc_id ON basicdb_flowable7.act_ru_event_subscr USING btree (proc_inst_id_);


--
-- TOC entry 4554 (class 1259 OID 4557027)
-- Name: act_idx_event_subscr_scoperef_; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_event_subscr_scoperef_ ON basicdb_flowable7.act_ru_event_subscr USING btree (scope_id_, scope_type_);


--
-- TOC entry 4564 (class 1259 OID 4557110)
-- Name: act_idx_exe_parent; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_exe_parent ON basicdb_flowable7.act_ru_execution USING btree (parent_id_);


--
-- TOC entry 4565 (class 1259 OID 4557122)
-- Name: act_idx_exe_procdef; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_exe_procdef ON basicdb_flowable7.act_ru_execution USING btree (proc_def_id_);


--
-- TOC entry 4566 (class 1259 OID 4557104)
-- Name: act_idx_exe_procinst; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_exe_procinst ON basicdb_flowable7.act_ru_execution USING btree (proc_inst_id_);


--
-- TOC entry 4567 (class 1259 OID 4557086)
-- Name: act_idx_exe_root; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_exe_root ON basicdb_flowable7.act_ru_execution USING btree (root_proc_inst_id_);


--
-- TOC entry 4568 (class 1259 OID 4557116)
-- Name: act_idx_exe_super; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_exe_super ON basicdb_flowable7.act_ru_execution USING btree (super_exec_);


--
-- TOC entry 4569 (class 1259 OID 4557085)
-- Name: act_idx_exec_buskey; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_exec_buskey ON basicdb_flowable7.act_ru_execution USING btree (business_key_);


--
-- TOC entry 4570 (class 1259 OID 4557087)
-- Name: act_idx_exec_ref_id_; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_exec_ref_id_ ON basicdb_flowable7.act_ru_execution USING btree (reference_id_);


--
-- TOC entry 4507 (class 1259 OID 4556867)
-- Name: act_idx_external_job_correlation_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_external_job_correlation_id ON basicdb_flowable7.act_ru_external_job USING btree (correlation_id_);


--
-- TOC entry 4508 (class 1259 OID 4556866)
-- Name: act_idx_external_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_external_job_custom_values_id ON basicdb_flowable7.act_ru_external_job USING btree (custom_values_id_);


--
-- TOC entry 4509 (class 1259 OID 4556865)
-- Name: act_idx_external_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_external_job_exception_stack_id ON basicdb_flowable7.act_ru_external_job USING btree (exception_stack_id_);


--
-- TOC entry 4603 (class 1259 OID 4557330)
-- Name: act_idx_hi_act_inst_end; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_act_inst_end ON basicdb_flowable7.act_hi_actinst USING btree (end_time_);


--
-- TOC entry 4604 (class 1259 OID 4557340)
-- Name: act_idx_hi_act_inst_exec; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_act_inst_exec ON basicdb_flowable7.act_hi_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 4605 (class 1259 OID 4557339)
-- Name: act_idx_hi_act_inst_procinst; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_act_inst_procinst ON basicdb_flowable7.act_hi_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 4606 (class 1259 OID 4557329)
-- Name: act_idx_hi_act_inst_start; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_act_inst_start ON basicdb_flowable7.act_hi_actinst USING btree (start_time_);


--
-- TOC entry 4609 (class 1259 OID 4557332)
-- Name: act_idx_hi_detail_act_inst; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_detail_act_inst ON basicdb_flowable7.act_hi_detail USING btree (act_inst_id_);


--
-- TOC entry 4610 (class 1259 OID 4557334)
-- Name: act_idx_hi_detail_name; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_detail_name ON basicdb_flowable7.act_hi_detail USING btree (name_);


--
-- TOC entry 4611 (class 1259 OID 4557331)
-- Name: act_idx_hi_detail_proc_inst; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_detail_proc_inst ON basicdb_flowable7.act_hi_detail USING btree (proc_inst_id_);


--
-- TOC entry 4612 (class 1259 OID 4557335)
-- Name: act_idx_hi_detail_task_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_detail_task_id ON basicdb_flowable7.act_hi_detail USING btree (task_id_);


--
-- TOC entry 4613 (class 1259 OID 4557333)
-- Name: act_idx_hi_detail_time; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_detail_time ON basicdb_flowable7.act_hi_detail USING btree (time_);


--
-- TOC entry 4435 (class 1259 OID 4556778)
-- Name: act_idx_hi_ent_lnk_ref_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_ent_lnk_ref_scope ON basicdb_flowable7.act_hi_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 4436 (class 1259 OID 4556779)
-- Name: act_idx_hi_ent_lnk_root_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_ent_lnk_root_scope ON basicdb_flowable7.act_hi_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 4437 (class 1259 OID 4556777)
-- Name: act_idx_hi_ent_lnk_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_ent_lnk_scope ON basicdb_flowable7.act_hi_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 4438 (class 1259 OID 4556780)
-- Name: act_idx_hi_ent_lnk_scope_def; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_ent_lnk_scope_def ON basicdb_flowable7.act_hi_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 4451 (class 1259 OID 4557342)
-- Name: act_idx_hi_ident_lnk_procinst; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_procinst ON basicdb_flowable7.act_hi_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 4452 (class 1259 OID 4556801)
-- Name: act_idx_hi_ident_lnk_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_scope ON basicdb_flowable7.act_hi_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 4453 (class 1259 OID 4556803)
-- Name: act_idx_hi_ident_lnk_scope_def; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_scope_def ON basicdb_flowable7.act_hi_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4454 (class 1259 OID 4556802)
-- Name: act_idx_hi_ident_lnk_sub_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_sub_scope ON basicdb_flowable7.act_hi_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4455 (class 1259 OID 4557341)
-- Name: act_idx_hi_ident_lnk_task; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_task ON basicdb_flowable7.act_hi_identitylink USING btree (task_id_);


--
-- TOC entry 4456 (class 1259 OID 4556800)
-- Name: act_idx_hi_ident_lnk_user; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_user ON basicdb_flowable7.act_hi_identitylink USING btree (user_id_);


--
-- TOC entry 4598 (class 1259 OID 4557327)
-- Name: act_idx_hi_pro_i_buskey; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_pro_i_buskey ON basicdb_flowable7.act_hi_procinst USING btree (business_key_);


--
-- TOC entry 4599 (class 1259 OID 4557326)
-- Name: act_idx_hi_pro_inst_end; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_pro_inst_end ON basicdb_flowable7.act_hi_procinst USING btree (end_time_);


--
-- TOC entry 4600 (class 1259 OID 4557328)
-- Name: act_idx_hi_pro_super_procinst; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_pro_super_procinst ON basicdb_flowable7.act_hi_procinst USING btree (super_process_instance_id_);


--
-- TOC entry 4545 (class 1259 OID 4557338)
-- Name: act_idx_hi_procvar_exe; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_procvar_exe ON basicdb_flowable7.act_hi_varinst USING btree (execution_id_);


--
-- TOC entry 4546 (class 1259 OID 4557013)
-- Name: act_idx_hi_procvar_name_type; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_procvar_name_type ON basicdb_flowable7.act_hi_varinst USING btree (name_, var_type_);


--
-- TOC entry 4547 (class 1259 OID 4557336)
-- Name: act_idx_hi_procvar_proc_inst; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_procvar_proc_inst ON basicdb_flowable7.act_hi_varinst USING btree (proc_inst_id_);


--
-- TOC entry 4548 (class 1259 OID 4557337)
-- Name: act_idx_hi_procvar_task_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_procvar_task_id ON basicdb_flowable7.act_hi_varinst USING btree (task_id_);


--
-- TOC entry 4528 (class 1259 OID 4557343)
-- Name: act_idx_hi_task_inst_procinst; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_task_inst_procinst ON basicdb_flowable7.act_hi_taskinst USING btree (proc_inst_id_);


--
-- TOC entry 4529 (class 1259 OID 4556986)
-- Name: act_idx_hi_task_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_task_scope ON basicdb_flowable7.act_hi_taskinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 4530 (class 1259 OID 4556988)
-- Name: act_idx_hi_task_scope_def; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_task_scope_def ON basicdb_flowable7.act_hi_taskinst USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4531 (class 1259 OID 4556987)
-- Name: act_idx_hi_task_sub_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_task_sub_scope ON basicdb_flowable7.act_hi_taskinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4549 (class 1259 OID 4557014)
-- Name: act_idx_hi_var_scope_id_type; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_var_scope_id_type ON basicdb_flowable7.act_hi_varinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 4550 (class 1259 OID 4557015)
-- Name: act_idx_hi_var_sub_id_type; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_hi_var_sub_id_type ON basicdb_flowable7.act_hi_varinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4440 (class 1259 OID 4556789)
-- Name: act_idx_ident_lnk_group; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ident_lnk_group ON basicdb_flowable7.act_ru_identitylink USING btree (group_id_);


--
-- TOC entry 4441 (class 1259 OID 4556790)
-- Name: act_idx_ident_lnk_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ident_lnk_scope ON basicdb_flowable7.act_ru_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 4442 (class 1259 OID 4556792)
-- Name: act_idx_ident_lnk_scope_def; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ident_lnk_scope_def ON basicdb_flowable7.act_ru_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4443 (class 1259 OID 4556791)
-- Name: act_idx_ident_lnk_sub_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ident_lnk_sub_scope ON basicdb_flowable7.act_ru_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4444 (class 1259 OID 4556788)
-- Name: act_idx_ident_lnk_user; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ident_lnk_user ON basicdb_flowable7.act_ru_identitylink USING btree (user_id_);


--
-- TOC entry 4445 (class 1259 OID 4557140)
-- Name: act_idx_idl_procinst; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_idl_procinst ON basicdb_flowable7.act_ru_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 4457 (class 1259 OID 4556854)
-- Name: act_idx_job_correlation_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_job_correlation_id ON basicdb_flowable7.act_ru_job USING btree (correlation_id_);


--
-- TOC entry 4458 (class 1259 OID 4556853)
-- Name: act_idx_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_job_custom_values_id ON basicdb_flowable7.act_ru_job USING btree (custom_values_id_);


--
-- TOC entry 4459 (class 1259 OID 4556852)
-- Name: act_idx_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_job_exception_stack_id ON basicdb_flowable7.act_ru_job USING btree (exception_stack_id_);


--
-- TOC entry 4460 (class 1259 OID 4557176)
-- Name: act_idx_job_execution_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_job_execution_id ON basicdb_flowable7.act_ru_job USING btree (execution_id_);


--
-- TOC entry 4461 (class 1259 OID 4557188)
-- Name: act_idx_job_proc_def_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_job_proc_def_id ON basicdb_flowable7.act_ru_job USING btree (proc_def_id_);


--
-- TOC entry 4462 (class 1259 OID 4557182)
-- Name: act_idx_job_process_instance_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_job_process_instance_id ON basicdb_flowable7.act_ru_job USING btree (process_instance_id_);


--
-- TOC entry 4463 (class 1259 OID 4556918)
-- Name: act_idx_job_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_job_scope ON basicdb_flowable7.act_ru_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4464 (class 1259 OID 4556920)
-- Name: act_idx_job_scope_def; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_job_scope_def ON basicdb_flowable7.act_ru_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4465 (class 1259 OID 4556919)
-- Name: act_idx_job_sub_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_job_sub_scope ON basicdb_flowable7.act_ru_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4559 (class 1259 OID 4557265)
-- Name: act_idx_model_deployment; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_model_deployment ON basicdb_flowable7.act_re_model USING btree (deployment_id_);


--
-- TOC entry 4560 (class 1259 OID 4557253)
-- Name: act_idx_model_source; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_model_source ON basicdb_flowable7.act_re_model USING btree (editor_source_value_id_);


--
-- TOC entry 4561 (class 1259 OID 4557259)
-- Name: act_idx_model_source_extra; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_model_source_extra ON basicdb_flowable7.act_re_model USING btree (editor_source_extra_value_id_);


--
-- TOC entry 4579 (class 1259 OID 4557271)
-- Name: act_idx_procdef_info_json; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_procdef_info_json ON basicdb_flowable7.act_procdef_info USING btree (info_json_id_);


--
-- TOC entry 4580 (class 1259 OID 4557277)
-- Name: act_idx_procdef_info_proc; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_procdef_info_proc ON basicdb_flowable7.act_procdef_info USING btree (proc_def_id_);


--
-- TOC entry 4585 (class 1259 OID 4557091)
-- Name: act_idx_ru_acti_end; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ru_acti_end ON basicdb_flowable7.act_ru_actinst USING btree (end_time_);


--
-- TOC entry 4586 (class 1259 OID 4557094)
-- Name: act_idx_ru_acti_exec; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ru_acti_exec ON basicdb_flowable7.act_ru_actinst USING btree (execution_id_);


--
-- TOC entry 4587 (class 1259 OID 4557095)
-- Name: act_idx_ru_acti_exec_act; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ru_acti_exec_act ON basicdb_flowable7.act_ru_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 4588 (class 1259 OID 4557092)
-- Name: act_idx_ru_acti_proc; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ru_acti_proc ON basicdb_flowable7.act_ru_actinst USING btree (proc_inst_id_);


--
-- TOC entry 4589 (class 1259 OID 4557093)
-- Name: act_idx_ru_acti_proc_act; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ru_acti_proc_act ON basicdb_flowable7.act_ru_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 4590 (class 1259 OID 4557090)
-- Name: act_idx_ru_acti_start; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ru_acti_start ON basicdb_flowable7.act_ru_actinst USING btree (start_time_);


--
-- TOC entry 4591 (class 1259 OID 4557096)
-- Name: act_idx_ru_acti_task; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ru_acti_task ON basicdb_flowable7.act_ru_actinst USING btree (task_id_);


--
-- TOC entry 4535 (class 1259 OID 4556997)
-- Name: act_idx_ru_var_scope_id_type; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ru_var_scope_id_type ON basicdb_flowable7.act_ru_variable USING btree (scope_id_, scope_type_);


--
-- TOC entry 4536 (class 1259 OID 4556998)
-- Name: act_idx_ru_var_sub_id_type; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_ru_var_sub_id_type ON basicdb_flowable7.act_ru_variable USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4480 (class 1259 OID 4556924)
-- Name: act_idx_sjob_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_sjob_scope ON basicdb_flowable7.act_ru_suspended_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4481 (class 1259 OID 4556926)
-- Name: act_idx_sjob_scope_def; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_sjob_scope_def ON basicdb_flowable7.act_ru_suspended_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4482 (class 1259 OID 4556925)
-- Name: act_idx_sjob_sub_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_sjob_sub_scope ON basicdb_flowable7.act_ru_suspended_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4483 (class 1259 OID 4556861)
-- Name: act_idx_suspended_job_correlation_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_suspended_job_correlation_id ON basicdb_flowable7.act_ru_suspended_job USING btree (correlation_id_);


--
-- TOC entry 4484 (class 1259 OID 4556860)
-- Name: act_idx_suspended_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_suspended_job_custom_values_id ON basicdb_flowable7.act_ru_suspended_job USING btree (custom_values_id_);


--
-- TOC entry 4485 (class 1259 OID 4556859)
-- Name: act_idx_suspended_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_suspended_job_exception_stack_id ON basicdb_flowable7.act_ru_suspended_job USING btree (exception_stack_id_);


--
-- TOC entry 4486 (class 1259 OID 4557212)
-- Name: act_idx_suspended_job_execution_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_suspended_job_execution_id ON basicdb_flowable7.act_ru_suspended_job USING btree (execution_id_);


--
-- TOC entry 4487 (class 1259 OID 4557224)
-- Name: act_idx_suspended_job_proc_def_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_suspended_job_proc_def_id ON basicdb_flowable7.act_ru_suspended_job USING btree (proc_def_id_);


--
-- TOC entry 4488 (class 1259 OID 4557218)
-- Name: act_idx_suspended_job_process_instance_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_suspended_job_process_instance_id ON basicdb_flowable7.act_ru_suspended_job USING btree (process_instance_id_);


--
-- TOC entry 4517 (class 1259 OID 4556963)
-- Name: act_idx_task_create; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_task_create ON basicdb_flowable7.act_ru_task USING btree (create_time_);


--
-- TOC entry 4518 (class 1259 OID 4557146)
-- Name: act_idx_task_exec; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_task_exec ON basicdb_flowable7.act_ru_task USING btree (execution_id_);


--
-- TOC entry 4519 (class 1259 OID 4557158)
-- Name: act_idx_task_procdef; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_task_procdef ON basicdb_flowable7.act_ru_task USING btree (proc_def_id_);


--
-- TOC entry 4520 (class 1259 OID 4557152)
-- Name: act_idx_task_procinst; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_task_procinst ON basicdb_flowable7.act_ru_task USING btree (proc_inst_id_);


--
-- TOC entry 4521 (class 1259 OID 4556964)
-- Name: act_idx_task_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_task_scope ON basicdb_flowable7.act_ru_task USING btree (scope_id_, scope_type_);


--
-- TOC entry 4522 (class 1259 OID 4556966)
-- Name: act_idx_task_scope_def; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_task_scope_def ON basicdb_flowable7.act_ru_task USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4523 (class 1259 OID 4556965)
-- Name: act_idx_task_sub_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_task_sub_scope ON basicdb_flowable7.act_ru_task USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4468 (class 1259 OID 4556857)
-- Name: act_idx_timer_job_correlation_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_timer_job_correlation_id ON basicdb_flowable7.act_ru_timer_job USING btree (correlation_id_);


--
-- TOC entry 4469 (class 1259 OID 4556856)
-- Name: act_idx_timer_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_timer_job_custom_values_id ON basicdb_flowable7.act_ru_timer_job USING btree (custom_values_id_);


--
-- TOC entry 4470 (class 1259 OID 4556858)
-- Name: act_idx_timer_job_duedate; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_timer_job_duedate ON basicdb_flowable7.act_ru_timer_job USING btree (duedate_);


--
-- TOC entry 4471 (class 1259 OID 4556855)
-- Name: act_idx_timer_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_timer_job_exception_stack_id ON basicdb_flowable7.act_ru_timer_job USING btree (exception_stack_id_);


--
-- TOC entry 4472 (class 1259 OID 4557194)
-- Name: act_idx_timer_job_execution_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_timer_job_execution_id ON basicdb_flowable7.act_ru_timer_job USING btree (execution_id_);


--
-- TOC entry 4473 (class 1259 OID 4557206)
-- Name: act_idx_timer_job_proc_def_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_timer_job_proc_def_id ON basicdb_flowable7.act_ru_timer_job USING btree (proc_def_id_);


--
-- TOC entry 4474 (class 1259 OID 4557200)
-- Name: act_idx_timer_job_process_instance_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_timer_job_process_instance_id ON basicdb_flowable7.act_ru_timer_job USING btree (process_instance_id_);


--
-- TOC entry 4475 (class 1259 OID 4556921)
-- Name: act_idx_tjob_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_tjob_scope ON basicdb_flowable7.act_ru_timer_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4476 (class 1259 OID 4556923)
-- Name: act_idx_tjob_scope_def; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_tjob_scope_def ON basicdb_flowable7.act_ru_timer_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4477 (class 1259 OID 4556922)
-- Name: act_idx_tjob_sub_scope; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_tjob_sub_scope ON basicdb_flowable7.act_ru_timer_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4446 (class 1259 OID 4557128)
-- Name: act_idx_tskass_task; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_tskass_task ON basicdb_flowable7.act_ru_identitylink USING btree (task_id_);


--
-- TOC entry 4537 (class 1259 OID 4556999)
-- Name: act_idx_var_bytearray; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_var_bytearray ON basicdb_flowable7.act_ru_variable USING btree (bytearray_id_);


--
-- TOC entry 4538 (class 1259 OID 4557164)
-- Name: act_idx_var_exe; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_var_exe ON basicdb_flowable7.act_ru_variable USING btree (execution_id_);


--
-- TOC entry 4539 (class 1259 OID 4557170)
-- Name: act_idx_var_procinst; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_var_procinst ON basicdb_flowable7.act_ru_variable USING btree (proc_inst_id_);


--
-- TOC entry 4540 (class 1259 OID 4557088)
-- Name: act_idx_variable_task_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX act_idx_variable_task_id ON basicdb_flowable7.act_ru_variable USING btree (task_id_);


--
-- TOC entry 4390 (class 1259 OID 4556653)
-- Name: dict_type; Type: INDEX; Schema: basicdb_flowable7; Owner: postgres
--

CREATE UNIQUE INDEX dict_type ON basicdb_flowable7.sys_dict_type USING btree (dict_type);


--
-- TOC entry 4514 (class 1259 OID 4556949)
-- Name: flw_idx_batch_part; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX flw_idx_batch_part ON basicdb_flowable7.flw_ru_batch_part USING btree (batch_id_);


--
-- TOC entry 4622 (class 1259 OID 4557370)
-- Name: flw_idx_event_rsrc_dpl; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX flw_idx_event_rsrc_dpl ON basicdb_flowable7.flw_event_resource USING btree (deployment_id_);


--
-- TOC entry 4661 (class 1259 OID 4557568)
-- Name: idx_extend_procinst_model_key; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_extend_procinst_model_key ON basicdb_flowable7.tbl_flow_extend_procinst USING btree (model_key);


--
-- TOC entry 4662 (class 1259 OID 4557567)
-- Name: idx_extend_procinst_proc_ins_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_extend_procinst_proc_ins_id ON basicdb_flowable7.tbl_flow_extend_procinst USING btree (proc_ins_id);


--
-- TOC entry 4629 (class 1259 OID 4557486)
-- Name: idx_wf_category_code; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_category_code ON basicdb_flowable7.wf_category USING btree (code);


--
-- TOC entry 4630 (class 1259 OID 4557485)
-- Name: idx_wf_category_parent; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_category_parent ON basicdb_flowable7.wf_category USING btree (parent);


--
-- TOC entry 4657 (class 1259 OID 4557558)
-- Name: idx_wf_comment_info_proc_ins_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_comment_info_proc_ins_id ON basicdb_flowable7.wf_comment_info USING btree (proc_ins_id);


--
-- TOC entry 4658 (class 1259 OID 4557559)
-- Name: idx_wf_comment_info_task_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_comment_info_task_id ON basicdb_flowable7.wf_comment_info USING btree (task_id);


--
-- TOC entry 4653 (class 1259 OID 4557549)
-- Name: idx_wf_copy_instance_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_copy_instance_id ON basicdb_flowable7.wf_copy USING btree (instance_id);


--
-- TOC entry 4654 (class 1259 OID 4557548)
-- Name: idx_wf_copy_user_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_copy_user_id ON basicdb_flowable7.wf_copy USING btree (user_id);


--
-- TOC entry 4644 (class 1259 OID 4557528)
-- Name: idx_wf_deploy_form_deploy_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_deploy_form_deploy_id ON basicdb_flowable7.wf_deploy_form USING btree (deploy_id);


--
-- TOC entry 4645 (class 1259 OID 4557529)
-- Name: idx_wf_deploy_form_form_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_deploy_form_form_id ON basicdb_flowable7.wf_deploy_form USING btree (form_id);


--
-- TOC entry 4635 (class 1259 OID 4557503)
-- Name: idx_wf_form_category_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_form_category_id ON basicdb_flowable7.wf_form USING btree (category_id);


--
-- TOC entry 4639 (class 1259 OID 4557512)
-- Name: idx_wf_form_category_parent; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_form_category_parent ON basicdb_flowable7.wf_form_category USING btree (parent);


--
-- TOC entry 4636 (class 1259 OID 4557502)
-- Name: idx_wf_form_code; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_form_code ON basicdb_flowable7.wf_form USING btree (code);


--
-- TOC entry 4648 (class 1259 OID 4557539)
-- Name: idx_wf_task_form_form_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_task_form_form_id ON basicdb_flowable7.wf_task_form USING btree (form_id);


--
-- TOC entry 4649 (class 1259 OID 4557537)
-- Name: idx_wf_task_form_proc_ins_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_task_form_proc_ins_id ON basicdb_flowable7.wf_task_form USING btree (proc_ins_id);


--
-- TOC entry 4650 (class 1259 OID 4557538)
-- Name: idx_wf_task_form_task_id; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE INDEX idx_wf_task_form_task_id ON basicdb_flowable7.wf_task_form USING btree (task_id);


--
-- TOC entry 4379 (class 1259 OID 4556654)
-- Name: sched_name; Type: INDEX; Schema: basicdb_flowable7; Owner: postgres
--

CREATE INDEX sched_name ON basicdb_flowable7.qrtz_triggers USING btree (sched_name, job_name, job_group);


--
-- TOC entry 4405 (class 1259 OID 4556655)
-- Name: uk_sys_notice_read_notice_user; Type: INDEX; Schema: basicdb_flowable7; Owner: system
--

CREATE UNIQUE INDEX uk_sys_notice_read_notice_user ON basicdb_flowable7.sys_notice_read USING btree (notice_id, user_id);


--
-- TOC entry 4666 (class 2606 OID 4557135)
-- Name: act_ru_identitylink act_fk_athrz_procedef; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_identitylink
    ADD CONSTRAINT act_fk_athrz_procedef FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable7.act_re_procdef(id_);


--
-- TOC entry 4665 (class 2606 OID 4557097)
-- Name: act_ge_bytearray act_fk_bytearr_depl; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ge_bytearray
    ADD CONSTRAINT act_fk_bytearr_depl FOREIGN KEY (deployment_id_) REFERENCES basicdb_flowable7.act_re_deployment(id_);


--
-- TOC entry 4684 (class 2606 OID 4556903)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4685 (class 2606 OID 4556898)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4686 (class 2606 OID 4557231)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_execution; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_execution FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4687 (class 2606 OID 4557243)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_proc_def; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable7.act_re_procdef(id_);


--
-- TOC entry 4688 (class 2606 OID 4557237)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_process_instance; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4698 (class 2606 OID 4557248)
-- Name: act_ru_event_subscr act_fk_event_exec; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_event_subscr
    ADD CONSTRAINT act_fk_event_exec FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4702 (class 2606 OID 4557111)
-- Name: act_ru_execution act_fk_exe_parent; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_execution
    ADD CONSTRAINT act_fk_exe_parent FOREIGN KEY (parent_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4703 (class 2606 OID 4557123)
-- Name: act_ru_execution act_fk_exe_procdef; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procdef FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable7.act_re_procdef(id_);


--
-- TOC entry 4704 (class 2606 OID 4557105)
-- Name: act_ru_execution act_fk_exe_procinst; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procinst FOREIGN KEY (proc_inst_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4705 (class 2606 OID 4557117)
-- Name: act_ru_execution act_fk_exe_super; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_execution
    ADD CONSTRAINT act_fk_exe_super FOREIGN KEY (super_exec_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4689 (class 2606 OID 4556913)
-- Name: act_ru_external_job act_fk_external_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4690 (class 2606 OID 4556908)
-- Name: act_ru_external_job act_fk_external_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4667 (class 2606 OID 4557141)
-- Name: act_ru_identitylink act_fk_idl_procinst; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_identitylink
    ADD CONSTRAINT act_fk_idl_procinst FOREIGN KEY (proc_inst_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4706 (class 2606 OID 4557272)
-- Name: act_procdef_info act_fk_info_json_ba; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_procdef_info
    ADD CONSTRAINT act_fk_info_json_ba FOREIGN KEY (info_json_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4707 (class 2606 OID 4557278)
-- Name: act_procdef_info act_fk_info_procdef; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_procdef_info
    ADD CONSTRAINT act_fk_info_procdef FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable7.act_re_procdef(id_);


--
-- TOC entry 4669 (class 2606 OID 4556873)
-- Name: act_ru_job act_fk_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_job
    ADD CONSTRAINT act_fk_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4670 (class 2606 OID 4556868)
-- Name: act_ru_job act_fk_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_job
    ADD CONSTRAINT act_fk_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4671 (class 2606 OID 4557177)
-- Name: act_ru_job act_fk_job_execution; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_job
    ADD CONSTRAINT act_fk_job_execution FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4672 (class 2606 OID 4557189)
-- Name: act_ru_job act_fk_job_proc_def; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_job
    ADD CONSTRAINT act_fk_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable7.act_re_procdef(id_);


--
-- TOC entry 4673 (class 2606 OID 4557183)
-- Name: act_ru_job act_fk_job_process_instance; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_job
    ADD CONSTRAINT act_fk_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4699 (class 2606 OID 4557266)
-- Name: act_re_model act_fk_model_deployment; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_re_model
    ADD CONSTRAINT act_fk_model_deployment FOREIGN KEY (deployment_id_) REFERENCES basicdb_flowable7.act_re_deployment(id_);


--
-- TOC entry 4700 (class 2606 OID 4557254)
-- Name: act_re_model act_fk_model_source; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_re_model
    ADD CONSTRAINT act_fk_model_source FOREIGN KEY (editor_source_value_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4701 (class 2606 OID 4557260)
-- Name: act_re_model act_fk_model_source_extra; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_re_model
    ADD CONSTRAINT act_fk_model_source_extra FOREIGN KEY (editor_source_extra_value_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4679 (class 2606 OID 4556893)
-- Name: act_ru_suspended_job act_fk_suspended_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4680 (class 2606 OID 4556888)
-- Name: act_ru_suspended_job act_fk_suspended_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4681 (class 2606 OID 4557213)
-- Name: act_ru_suspended_job act_fk_suspended_job_execution; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_execution FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4682 (class 2606 OID 4557225)
-- Name: act_ru_suspended_job act_fk_suspended_job_proc_def; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable7.act_re_procdef(id_);


--
-- TOC entry 4683 (class 2606 OID 4557219)
-- Name: act_ru_suspended_job act_fk_suspended_job_process_instance; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4692 (class 2606 OID 4557147)
-- Name: act_ru_task act_fk_task_exe; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_task
    ADD CONSTRAINT act_fk_task_exe FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4693 (class 2606 OID 4557159)
-- Name: act_ru_task act_fk_task_procdef; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_task
    ADD CONSTRAINT act_fk_task_procdef FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable7.act_re_procdef(id_);


--
-- TOC entry 4694 (class 2606 OID 4557153)
-- Name: act_ru_task act_fk_task_procinst; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_task
    ADD CONSTRAINT act_fk_task_procinst FOREIGN KEY (proc_inst_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4674 (class 2606 OID 4556883)
-- Name: act_ru_timer_job act_fk_timer_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4675 (class 2606 OID 4556878)
-- Name: act_ru_timer_job act_fk_timer_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4676 (class 2606 OID 4557195)
-- Name: act_ru_timer_job act_fk_timer_job_execution; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_execution FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4677 (class 2606 OID 4557207)
-- Name: act_ru_timer_job act_fk_timer_job_proc_def; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable7.act_re_procdef(id_);


--
-- TOC entry 4678 (class 2606 OID 4557201)
-- Name: act_ru_timer_job act_fk_timer_job_process_instance; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4668 (class 2606 OID 4557129)
-- Name: act_ru_identitylink act_fk_tskass_task; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_identitylink
    ADD CONSTRAINT act_fk_tskass_task FOREIGN KEY (task_id_) REFERENCES basicdb_flowable7.act_ru_task(id_);


--
-- TOC entry 4695 (class 2606 OID 4557000)
-- Name: act_ru_variable act_fk_var_bytearray; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_variable
    ADD CONSTRAINT act_fk_var_bytearray FOREIGN KEY (bytearray_id_) REFERENCES basicdb_flowable7.act_ge_bytearray(id_);


--
-- TOC entry 4696 (class 2606 OID 4557165)
-- Name: act_ru_variable act_fk_var_exe; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_variable
    ADD CONSTRAINT act_fk_var_exe FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4697 (class 2606 OID 4557171)
-- Name: act_ru_variable act_fk_var_procinst; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.act_ru_variable
    ADD CONSTRAINT act_fk_var_procinst FOREIGN KEY (proc_inst_id_) REFERENCES basicdb_flowable7.act_ru_execution(id_);


--
-- TOC entry 4691 (class 2606 OID 4556950)
-- Name: flw_ru_batch_part flw_fk_batch_part_parent; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.flw_ru_batch_part
    ADD CONSTRAINT flw_fk_batch_part_parent FOREIGN KEY (batch_id_) REFERENCES basicdb_flowable7.flw_ru_batch(id_);


--
-- TOC entry 4708 (class 2606 OID 4557365)
-- Name: flw_event_resource flw_fk_event_rsrc_dpl; Type: FK CONSTRAINT; Schema: basicdb_flowable7; Owner: system
--

ALTER TABLE ONLY basicdb_flowable7.flw_event_resource
    ADD CONSTRAINT flw_fk_event_rsrc_dpl FOREIGN KEY (deployment_id_) REFERENCES basicdb_flowable7.flw_event_deployment(id_);


--
-- TOC entry 4970 (class 0 OID 0)
-- Dependencies: 488
-- Name: TABLE gen_table; Type: ACL; Schema: basicdb_flowable7; Owner: system
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.gen_table TO PUBLIC;


--
-- TOC entry 4971 (class 0 OID 0)
-- Dependencies: 489
-- Name: TABLE gen_table_column; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.gen_table_column TO PUBLIC;


--
-- TOC entry 4973 (class 0 OID 0)
-- Dependencies: 490
-- Name: SEQUENCE gen_table_column_column_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.gen_table_column_column_id_seq TO PUBLIC;


--
-- TOC entry 4975 (class 0 OID 0)
-- Dependencies: 491
-- Name: SEQUENCE gen_table_table_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: system
--

GRANT ALL ON SEQUENCE basicdb_flowable7.gen_table_table_id_seq TO PUBLIC;


--
-- TOC entry 4976 (class 0 OID 0)
-- Dependencies: 492
-- Name: TABLE list_column; Type: ACL; Schema: basicdb_flowable7; Owner: system
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.list_column TO PUBLIC;


--
-- TOC entry 4977 (class 0 OID 0)
-- Dependencies: 493
-- Name: TABLE list_table; Type: ACL; Schema: basicdb_flowable7; Owner: system
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.list_table TO PUBLIC;


--
-- TOC entry 4978 (class 0 OID 0)
-- Dependencies: 494
-- Name: TABLE qrtz_blob_triggers; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.qrtz_blob_triggers TO PUBLIC;


--
-- TOC entry 4979 (class 0 OID 0)
-- Dependencies: 495
-- Name: TABLE qrtz_calendars; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.qrtz_calendars TO PUBLIC;


--
-- TOC entry 4980 (class 0 OID 0)
-- Dependencies: 496
-- Name: TABLE qrtz_cron_triggers; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.qrtz_cron_triggers TO PUBLIC;


--
-- TOC entry 4981 (class 0 OID 0)
-- Dependencies: 497
-- Name: TABLE qrtz_fired_triggers; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.qrtz_fired_triggers TO PUBLIC;


--
-- TOC entry 4982 (class 0 OID 0)
-- Dependencies: 498
-- Name: TABLE qrtz_job_details; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.qrtz_job_details TO PUBLIC;


--
-- TOC entry 4983 (class 0 OID 0)
-- Dependencies: 499
-- Name: TABLE qrtz_locks; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.qrtz_locks TO PUBLIC;


--
-- TOC entry 4984 (class 0 OID 0)
-- Dependencies: 500
-- Name: TABLE qrtz_paused_trigger_grps; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.qrtz_paused_trigger_grps TO PUBLIC;


--
-- TOC entry 4985 (class 0 OID 0)
-- Dependencies: 501
-- Name: TABLE qrtz_scheduler_state; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.qrtz_scheduler_state TO PUBLIC;


--
-- TOC entry 4986 (class 0 OID 0)
-- Dependencies: 502
-- Name: TABLE qrtz_simple_triggers; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.qrtz_simple_triggers TO PUBLIC;


--
-- TOC entry 4987 (class 0 OID 0)
-- Dependencies: 503
-- Name: TABLE qrtz_simprop_triggers; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.qrtz_simprop_triggers TO PUBLIC;


--
-- TOC entry 4988 (class 0 OID 0)
-- Dependencies: 504
-- Name: TABLE qrtz_triggers; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.qrtz_triggers TO PUBLIC;


--
-- TOC entry 4989 (class 0 OID 0)
-- Dependencies: 505
-- Name: TABLE sys_config; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_config TO PUBLIC;


--
-- TOC entry 4991 (class 0 OID 0)
-- Dependencies: 506
-- Name: SEQUENCE sys_config_config_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_config_config_id_seq TO PUBLIC;


--
-- TOC entry 4992 (class 0 OID 0)
-- Dependencies: 508
-- Name: TABLE sys_dept; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_dept TO PUBLIC;


--
-- TOC entry 4994 (class 0 OID 0)
-- Dependencies: 509
-- Name: SEQUENCE sys_dept_dept_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_dept_dept_id_seq TO PUBLIC;


--
-- TOC entry 4995 (class 0 OID 0)
-- Dependencies: 510
-- Name: TABLE sys_dict_data; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_dict_data TO PUBLIC;


--
-- TOC entry 4997 (class 0 OID 0)
-- Dependencies: 511
-- Name: SEQUENCE sys_dict_data_dict_code_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_dict_data_dict_code_seq TO PUBLIC;


--
-- TOC entry 4998 (class 0 OID 0)
-- Dependencies: 512
-- Name: TABLE sys_dict_type; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_dict_type TO PUBLIC;


--
-- TOC entry 5000 (class 0 OID 0)
-- Dependencies: 513
-- Name: SEQUENCE sys_dict_type_dict_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_dict_type_dict_id_seq TO PUBLIC;


--
-- TOC entry 5004 (class 0 OID 0)
-- Dependencies: 516
-- Name: TABLE sys_job; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_job TO PUBLIC;


--
-- TOC entry 5006 (class 0 OID 0)
-- Dependencies: 517
-- Name: SEQUENCE sys_job_job_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_job_job_id_seq TO PUBLIC;


--
-- TOC entry 5007 (class 0 OID 0)
-- Dependencies: 518
-- Name: TABLE sys_job_log; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_job_log TO PUBLIC;


--
-- TOC entry 5009 (class 0 OID 0)
-- Dependencies: 519
-- Name: SEQUENCE sys_job_log_job_log_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_job_log_job_log_id_seq TO PUBLIC;


--
-- TOC entry 5010 (class 0 OID 0)
-- Dependencies: 520
-- Name: TABLE sys_logininfor; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_logininfor TO PUBLIC;


--
-- TOC entry 5012 (class 0 OID 0)
-- Dependencies: 521
-- Name: SEQUENCE sys_logininfor_info_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_logininfor_info_id_seq TO PUBLIC;


--
-- TOC entry 5014 (class 0 OID 0)
-- Dependencies: 522
-- Name: TABLE sys_menu; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_menu TO PUBLIC;


--
-- TOC entry 5016 (class 0 OID 0)
-- Dependencies: 523
-- Name: SEQUENCE sys_menu_menu_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_menu_menu_id_seq TO PUBLIC;


--
-- TOC entry 5017 (class 0 OID 0)
-- Dependencies: 524
-- Name: TABLE sys_notice; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_notice TO PUBLIC;


--
-- TOC entry 5019 (class 0 OID 0)
-- Dependencies: 525
-- Name: SEQUENCE sys_notice_notice_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_notice_notice_id_seq TO PUBLIC;


--
-- TOC entry 5021 (class 0 OID 0)
-- Dependencies: 528
-- Name: TABLE sys_oper_log; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_oper_log TO PUBLIC;


--
-- TOC entry 5023 (class 0 OID 0)
-- Dependencies: 529
-- Name: SEQUENCE sys_oper_log_oper_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_oper_log_oper_id_seq TO PUBLIC;


--
-- TOC entry 5024 (class 0 OID 0)
-- Dependencies: 530
-- Name: TABLE sys_post; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_post TO PUBLIC;


--
-- TOC entry 5026 (class 0 OID 0)
-- Dependencies: 531
-- Name: SEQUENCE sys_post_post_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_post_post_id_seq TO PUBLIC;


--
-- TOC entry 5027 (class 0 OID 0)
-- Dependencies: 532
-- Name: TABLE sys_role; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_role TO PUBLIC;


--
-- TOC entry 5028 (class 0 OID 0)
-- Dependencies: 533
-- Name: TABLE sys_role_dept; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_role_dept TO PUBLIC;


--
-- TOC entry 5029 (class 0 OID 0)
-- Dependencies: 534
-- Name: TABLE sys_role_menu; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_role_menu TO PUBLIC;


--
-- TOC entry 5031 (class 0 OID 0)
-- Dependencies: 535
-- Name: SEQUENCE sys_role_role_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_role_role_id_seq TO PUBLIC;


--
-- TOC entry 5032 (class 0 OID 0)
-- Dependencies: 536
-- Name: TABLE sys_user; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_user TO PUBLIC;


--
-- TOC entry 5033 (class 0 OID 0)
-- Dependencies: 537
-- Name: TABLE sys_user_post; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_user_post TO PUBLIC;


--
-- TOC entry 5034 (class 0 OID 0)
-- Dependencies: 538
-- Name: TABLE sys_user_role; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable7.sys_user_role TO PUBLIC;


--
-- TOC entry 5036 (class 0 OID 0)
-- Dependencies: 539
-- Name: SEQUENCE sys_user_user_id_seq; Type: ACL; Schema: basicdb_flowable7; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable7.sys_user_user_id_seq TO PUBLIC;


-- Completed on 2026-10-23 17:16:27

--
-- PostgreSQL database dump complete
--

