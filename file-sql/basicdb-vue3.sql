--
-- PostgreSQL database dump
--

-- Dumped from database version 15.1
-- Dumped by pg_dump version 17.0

-- Started on 2026-10-17 21:26:28

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

ALTER TABLE ONLY basicdb_flowable8.flw_event_resource DROP CONSTRAINT flw_fk_event_rsrc_dpl;
ALTER TABLE ONLY basicdb_flowable8.flw_ru_batch_part DROP CONSTRAINT flw_fk_batch_part_parent;
ALTER TABLE ONLY basicdb_flowable8.act_ru_variable DROP CONSTRAINT act_fk_var_procinst;
ALTER TABLE ONLY basicdb_flowable8.act_ru_variable DROP CONSTRAINT act_fk_var_exe;
ALTER TABLE ONLY basicdb_flowable8.act_ru_variable DROP CONSTRAINT act_fk_var_bytearray;
ALTER TABLE ONLY basicdb_flowable8.act_ru_identitylink DROP CONSTRAINT act_fk_tskass_task;
ALTER TABLE ONLY basicdb_flowable8.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_process_instance;
ALTER TABLE ONLY basicdb_flowable8.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_proc_def;
ALTER TABLE ONLY basicdb_flowable8.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_execution;
ALTER TABLE ONLY basicdb_flowable8.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_exception;
ALTER TABLE ONLY basicdb_flowable8.act_ru_timer_job DROP CONSTRAINT act_fk_timer_job_custom_values;
ALTER TABLE ONLY basicdb_flowable8.act_ru_task DROP CONSTRAINT act_fk_task_procinst;
ALTER TABLE ONLY basicdb_flowable8.act_ru_task DROP CONSTRAINT act_fk_task_procdef;
ALTER TABLE ONLY basicdb_flowable8.act_ru_task DROP CONSTRAINT act_fk_task_exe;
ALTER TABLE ONLY basicdb_flowable8.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_process_instance;
ALTER TABLE ONLY basicdb_flowable8.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_proc_def;
ALTER TABLE ONLY basicdb_flowable8.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_execution;
ALTER TABLE ONLY basicdb_flowable8.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_exception;
ALTER TABLE ONLY basicdb_flowable8.act_ru_suspended_job DROP CONSTRAINT act_fk_suspended_job_custom_values;
ALTER TABLE ONLY basicdb_flowable8.act_id_priv_mapping DROP CONSTRAINT act_fk_priv_mapping;
ALTER TABLE ONLY basicdb_flowable8.act_re_model DROP CONSTRAINT act_fk_model_source_extra;
ALTER TABLE ONLY basicdb_flowable8.act_re_model DROP CONSTRAINT act_fk_model_source;
ALTER TABLE ONLY basicdb_flowable8.act_re_model DROP CONSTRAINT act_fk_model_deployment;
ALTER TABLE ONLY basicdb_flowable8.act_id_membership DROP CONSTRAINT act_fk_memb_user;
ALTER TABLE ONLY basicdb_flowable8.act_id_membership DROP CONSTRAINT act_fk_memb_group;
ALTER TABLE ONLY basicdb_flowable8.act_ru_job DROP CONSTRAINT act_fk_job_process_instance;
ALTER TABLE ONLY basicdb_flowable8.act_ru_job DROP CONSTRAINT act_fk_job_proc_def;
ALTER TABLE ONLY basicdb_flowable8.act_ru_job DROP CONSTRAINT act_fk_job_execution;
ALTER TABLE ONLY basicdb_flowable8.act_ru_job DROP CONSTRAINT act_fk_job_exception;
ALTER TABLE ONLY basicdb_flowable8.act_ru_job DROP CONSTRAINT act_fk_job_custom_values;
ALTER TABLE ONLY basicdb_flowable8.act_procdef_info DROP CONSTRAINT act_fk_info_procdef;
ALTER TABLE ONLY basicdb_flowable8.act_procdef_info DROP CONSTRAINT act_fk_info_json_ba;
ALTER TABLE ONLY basicdb_flowable8.act_ru_identitylink DROP CONSTRAINT act_fk_idl_procinst;
ALTER TABLE ONLY basicdb_flowable8.act_ru_external_job DROP CONSTRAINT act_fk_external_job_exception;
ALTER TABLE ONLY basicdb_flowable8.act_ru_external_job DROP CONSTRAINT act_fk_external_job_custom_values;
ALTER TABLE ONLY basicdb_flowable8.act_ru_execution DROP CONSTRAINT act_fk_exe_super;
ALTER TABLE ONLY basicdb_flowable8.act_ru_execution DROP CONSTRAINT act_fk_exe_procinst;
ALTER TABLE ONLY basicdb_flowable8.act_ru_execution DROP CONSTRAINT act_fk_exe_procdef;
ALTER TABLE ONLY basicdb_flowable8.act_ru_execution DROP CONSTRAINT act_fk_exe_parent;
ALTER TABLE ONLY basicdb_flowable8.act_ru_event_subscr DROP CONSTRAINT act_fk_event_exec;
ALTER TABLE ONLY basicdb_flowable8.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_process_instance;
ALTER TABLE ONLY basicdb_flowable8.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_proc_def;
ALTER TABLE ONLY basicdb_flowable8.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_execution;
ALTER TABLE ONLY basicdb_flowable8.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_exception;
ALTER TABLE ONLY basicdb_flowable8.act_ru_deadletter_job DROP CONSTRAINT act_fk_deadletter_job_custom_values;
ALTER TABLE ONLY basicdb_flowable8.act_ge_bytearray DROP CONSTRAINT act_fk_bytearr_depl;
ALTER TABLE ONLY basicdb_flowable8.act_ru_identitylink DROP CONSTRAINT act_fk_athrz_procedef;
DROP INDEX basicdb_flowable8.uk_wf_form_code;
DROP INDEX basicdb_flowable8.uk_sys_notice_read_notice_user;
DROP INDEX basicdb_flowable8.sched_name;
DROP INDEX basicdb_flowable8.idx_wf_task_form_task;
DROP INDEX basicdb_flowable8.idx_wf_task_form_proc_ins;
DROP INDEX basicdb_flowable8.idx_wf_task_form_act;
DROP INDEX basicdb_flowable8.idx_wf_deploy_form_form;
DROP INDEX basicdb_flowable8.idx_wf_deploy_form_deploy;
DROP INDEX basicdb_flowable8.idx_wf_deploy_form_act;
DROP INDEX basicdb_flowable8.idx_wf_copy_instance;
DROP INDEX basicdb_flowable8.idx_wf_comment_info_proc;
DROP INDEX basicdb_flowable8.idx_extend_procinst_proc_ins;
DROP INDEX basicdb_flowable8.idx_extend_procinst_model_key;
DROP INDEX basicdb_flowable8.flw_idx_event_rsrc_dpl;
DROP INDEX basicdb_flowable8.flw_idx_batch_part;
DROP INDEX basicdb_flowable8.dict_type;
DROP INDEX basicdb_flowable8.act_idx_variable_task_id;
DROP INDEX basicdb_flowable8.act_idx_var_procinst;
DROP INDEX basicdb_flowable8.act_idx_var_exe;
DROP INDEX basicdb_flowable8.act_idx_var_bytearray;
DROP INDEX basicdb_flowable8.act_idx_tskass_task;
DROP INDEX basicdb_flowable8.act_idx_tjob_sub_scope;
DROP INDEX basicdb_flowable8.act_idx_tjob_scope_def;
DROP INDEX basicdb_flowable8.act_idx_tjob_scope;
DROP INDEX basicdb_flowable8.act_idx_timer_job_process_instance_id;
DROP INDEX basicdb_flowable8.act_idx_timer_job_proc_def_id;
DROP INDEX basicdb_flowable8.act_idx_timer_job_execution_id;
DROP INDEX basicdb_flowable8.act_idx_timer_job_exception_stack_id;
DROP INDEX basicdb_flowable8.act_idx_timer_job_duedate;
DROP INDEX basicdb_flowable8.act_idx_timer_job_custom_values_id;
DROP INDEX basicdb_flowable8.act_idx_timer_job_correlation_id;
DROP INDEX basicdb_flowable8.act_idx_task_sub_scope;
DROP INDEX basicdb_flowable8.act_idx_task_scope_def;
DROP INDEX basicdb_flowable8.act_idx_task_scope;
DROP INDEX basicdb_flowable8.act_idx_task_procinst;
DROP INDEX basicdb_flowable8.act_idx_task_procdef;
DROP INDEX basicdb_flowable8.act_idx_task_exec;
DROP INDEX basicdb_flowable8.act_idx_task_create;
DROP INDEX basicdb_flowable8.act_idx_suspended_job_process_instance_id;
DROP INDEX basicdb_flowable8.act_idx_suspended_job_proc_def_id;
DROP INDEX basicdb_flowable8.act_idx_suspended_job_execution_id;
DROP INDEX basicdb_flowable8.act_idx_suspended_job_exception_stack_id;
DROP INDEX basicdb_flowable8.act_idx_suspended_job_custom_values_id;
DROP INDEX basicdb_flowable8.act_idx_suspended_job_correlation_id;
DROP INDEX basicdb_flowable8.act_idx_sjob_sub_scope;
DROP INDEX basicdb_flowable8.act_idx_sjob_scope_def;
DROP INDEX basicdb_flowable8.act_idx_sjob_scope;
DROP INDEX basicdb_flowable8.act_idx_ru_var_sub_id_type;
DROP INDEX basicdb_flowable8.act_idx_ru_var_scope_id_type;
DROP INDEX basicdb_flowable8.act_idx_ru_acti_task;
DROP INDEX basicdb_flowable8.act_idx_ru_acti_start;
DROP INDEX basicdb_flowable8.act_idx_ru_acti_proc_act;
DROP INDEX basicdb_flowable8.act_idx_ru_acti_proc;
DROP INDEX basicdb_flowable8.act_idx_ru_acti_exec_act;
DROP INDEX basicdb_flowable8.act_idx_ru_acti_exec;
DROP INDEX basicdb_flowable8.act_idx_ru_acti_end;
DROP INDEX basicdb_flowable8.act_idx_procdef_info_proc;
DROP INDEX basicdb_flowable8.act_idx_procdef_info_json;
DROP INDEX basicdb_flowable8.act_idx_priv_user;
DROP INDEX basicdb_flowable8.act_idx_priv_mapping;
DROP INDEX basicdb_flowable8.act_idx_priv_group;
DROP INDEX basicdb_flowable8.act_idx_model_source_extra;
DROP INDEX basicdb_flowable8.act_idx_model_source;
DROP INDEX basicdb_flowable8.act_idx_model_deployment;
DROP INDEX basicdb_flowable8.act_idx_memb_user;
DROP INDEX basicdb_flowable8.act_idx_memb_group;
DROP INDEX basicdb_flowable8.act_idx_job_sub_scope;
DROP INDEX basicdb_flowable8.act_idx_job_scope_def;
DROP INDEX basicdb_flowable8.act_idx_job_scope;
DROP INDEX basicdb_flowable8.act_idx_job_process_instance_id;
DROP INDEX basicdb_flowable8.act_idx_job_proc_def_id;
DROP INDEX basicdb_flowable8.act_idx_job_execution_id;
DROP INDEX basicdb_flowable8.act_idx_job_exception_stack_id;
DROP INDEX basicdb_flowable8.act_idx_job_custom_values_id;
DROP INDEX basicdb_flowable8.act_idx_job_correlation_id;
DROP INDEX basicdb_flowable8.act_idx_idl_procinst;
DROP INDEX basicdb_flowable8.act_idx_ident_lnk_user;
DROP INDEX basicdb_flowable8.act_idx_ident_lnk_sub_scope;
DROP INDEX basicdb_flowable8.act_idx_ident_lnk_scope_def;
DROP INDEX basicdb_flowable8.act_idx_ident_lnk_scope;
DROP INDEX basicdb_flowable8.act_idx_ident_lnk_group;
DROP INDEX basicdb_flowable8.act_idx_hi_var_sub_id_type;
DROP INDEX basicdb_flowable8.act_idx_hi_var_scope_id_type;
DROP INDEX basicdb_flowable8.act_idx_hi_task_sub_scope;
DROP INDEX basicdb_flowable8.act_idx_hi_task_scope_def;
DROP INDEX basicdb_flowable8.act_idx_hi_task_scope;
DROP INDEX basicdb_flowable8.act_idx_hi_task_inst_procinst;
DROP INDEX basicdb_flowable8.act_idx_hi_procvar_task_id;
DROP INDEX basicdb_flowable8.act_idx_hi_procvar_proc_inst;
DROP INDEX basicdb_flowable8.act_idx_hi_procvar_name_type;
DROP INDEX basicdb_flowable8.act_idx_hi_procvar_exe;
DROP INDEX basicdb_flowable8.act_idx_hi_pro_super_procinst;
DROP INDEX basicdb_flowable8.act_idx_hi_pro_inst_end;
DROP INDEX basicdb_flowable8.act_idx_hi_pro_i_buskey;
DROP INDEX basicdb_flowable8.act_idx_hi_ident_lnk_user;
DROP INDEX basicdb_flowable8.act_idx_hi_ident_lnk_task;
DROP INDEX basicdb_flowable8.act_idx_hi_ident_lnk_sub_scope;
DROP INDEX basicdb_flowable8.act_idx_hi_ident_lnk_scope_def;
DROP INDEX basicdb_flowable8.act_idx_hi_ident_lnk_scope;
DROP INDEX basicdb_flowable8.act_idx_hi_ident_lnk_procinst;
DROP INDEX basicdb_flowable8.act_idx_hi_ent_lnk_scope_def;
DROP INDEX basicdb_flowable8.act_idx_hi_ent_lnk_scope;
DROP INDEX basicdb_flowable8.act_idx_hi_ent_lnk_root_scope;
DROP INDEX basicdb_flowable8.act_idx_hi_ent_lnk_ref_scope;
DROP INDEX basicdb_flowable8.act_idx_hi_detail_time;
DROP INDEX basicdb_flowable8.act_idx_hi_detail_task_id;
DROP INDEX basicdb_flowable8.act_idx_hi_detail_proc_inst;
DROP INDEX basicdb_flowable8.act_idx_hi_detail_name;
DROP INDEX basicdb_flowable8.act_idx_hi_detail_act_inst;
DROP INDEX basicdb_flowable8.act_idx_hi_act_inst_start;
DROP INDEX basicdb_flowable8.act_idx_hi_act_inst_procinst;
DROP INDEX basicdb_flowable8.act_idx_hi_act_inst_exec;
DROP INDEX basicdb_flowable8.act_idx_hi_act_inst_end;
DROP INDEX basicdb_flowable8.act_idx_external_job_exception_stack_id;
DROP INDEX basicdb_flowable8.act_idx_external_job_custom_values_id;
DROP INDEX basicdb_flowable8.act_idx_external_job_correlation_id;
DROP INDEX basicdb_flowable8.act_idx_exec_ref_id_;
DROP INDEX basicdb_flowable8.act_idx_exec_buskey;
DROP INDEX basicdb_flowable8.act_idx_exe_super;
DROP INDEX basicdb_flowable8.act_idx_exe_root;
DROP INDEX basicdb_flowable8.act_idx_exe_procinst;
DROP INDEX basicdb_flowable8.act_idx_exe_procdef;
DROP INDEX basicdb_flowable8.act_idx_exe_parent;
DROP INDEX basicdb_flowable8.act_idx_event_subscr_scoperef_;
DROP INDEX basicdb_flowable8.act_idx_event_subscr_proc_id;
DROP INDEX basicdb_flowable8.act_idx_event_subscr_config_;
DROP INDEX basicdb_flowable8.act_idx_event_subscr;
DROP INDEX basicdb_flowable8.act_idx_event_def_uniq;
DROP INDEX basicdb_flowable8.act_idx_ent_lnk_scope_def;
DROP INDEX basicdb_flowable8.act_idx_ent_lnk_scope;
DROP INDEX basicdb_flowable8.act_idx_ent_lnk_root_scope;
DROP INDEX basicdb_flowable8.act_idx_ent_lnk_ref_scope;
DROP INDEX basicdb_flowable8.act_idx_ejob_sub_scope;
DROP INDEX basicdb_flowable8.act_idx_ejob_scope_def;
DROP INDEX basicdb_flowable8.act_idx_ejob_scope;
DROP INDEX basicdb_flowable8.act_idx_djob_sub_scope;
DROP INDEX basicdb_flowable8.act_idx_djob_scope_def;
DROP INDEX basicdb_flowable8.act_idx_djob_scope;
DROP INDEX basicdb_flowable8.act_idx_deadletter_job_process_instance_id;
DROP INDEX basicdb_flowable8.act_idx_deadletter_job_proc_def_id;
DROP INDEX basicdb_flowable8.act_idx_deadletter_job_execution_id;
DROP INDEX basicdb_flowable8.act_idx_deadletter_job_exception_stack_id;
DROP INDEX basicdb_flowable8.act_idx_deadletter_job_custom_values_id;
DROP INDEX basicdb_flowable8.act_idx_deadletter_job_correlation_id;
DROP INDEX basicdb_flowable8.act_idx_channel_def_uniq;
DROP INDEX basicdb_flowable8.act_idx_bytear_depl;
DROP INDEX basicdb_flowable8.act_idx_athrz_procedef;
DROP INDEX basicdb_flowable8.act_idx_act_hi_tsk_log_task;
ALTER TABLE ONLY basicdb_flowable8.sys_notice_read DROP CONSTRAINT sys_notice_read_pkey;
ALTER TABLE ONLY basicdb_flowable8.sys_files DROP CONSTRAINT sys_files_pkey;
ALTER TABLE ONLY basicdb_flowable8.sys_demo DROP CONSTRAINT sys_demo_pkey;
ALTER TABLE ONLY basicdb_flowable8.wf_task_form DROP CONSTRAINT pk_wf_task_form;
ALTER TABLE ONLY basicdb_flowable8.wf_form_template DROP CONSTRAINT pk_wf_form_template;
ALTER TABLE ONLY basicdb_flowable8.wf_form_category DROP CONSTRAINT pk_wf_form_category;
ALTER TABLE ONLY basicdb_flowable8.wf_form DROP CONSTRAINT pk_wf_form;
ALTER TABLE ONLY basicdb_flowable8.wf_deploy_form DROP CONSTRAINT pk_wf_deploy_form;
ALTER TABLE ONLY basicdb_flowable8.wf_copy DROP CONSTRAINT pk_wf_copy;
ALTER TABLE ONLY basicdb_flowable8.wf_comment_info DROP CONSTRAINT pk_wf_comment_info;
ALTER TABLE ONLY basicdb_flowable8.wf_category DROP CONSTRAINT pk_wf_category;
ALTER TABLE ONLY basicdb_flowable8.tbl_flow_extend_procinst DROP CONSTRAINT pk_tbl_flow_extend_procinst;
ALTER TABLE ONLY basicdb_flowable8.flw_expression DROP CONSTRAINT pk_flw_expression;
ALTER TABLE ONLY basicdb_flowable8.flw_ru_batch DROP CONSTRAINT flw_ru_batch_pkey;
ALTER TABLE ONLY basicdb_flowable8.flw_ru_batch_part DROP CONSTRAINT flw_ru_batch_part_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_re_procdef DROP CONSTRAINT act_uniq_procdef;
ALTER TABLE ONLY basicdb_flowable8.act_id_priv DROP CONSTRAINT act_uniq_priv_name;
ALTER TABLE ONLY basicdb_flowable8.act_procdef_info DROP CONSTRAINT act_uniq_info_procdef;
ALTER TABLE ONLY basicdb_flowable8.act_ru_variable DROP CONSTRAINT act_ru_variable_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ru_timer_job DROP CONSTRAINT act_ru_timer_job_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ru_task DROP CONSTRAINT act_ru_task_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ru_suspended_job DROP CONSTRAINT act_ru_suspended_job_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ru_job DROP CONSTRAINT act_ru_job_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ru_identitylink DROP CONSTRAINT act_ru_identitylink_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ru_history_job DROP CONSTRAINT act_ru_history_job_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ru_external_job DROP CONSTRAINT act_ru_external_job_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ru_execution DROP CONSTRAINT act_ru_execution_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ru_event_subscr DROP CONSTRAINT act_ru_event_subscr_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ru_entitylink DROP CONSTRAINT act_ru_entitylink_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ru_deadletter_job DROP CONSTRAINT act_ru_deadletter_job_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ru_actinst DROP CONSTRAINT act_ru_actinst_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_re_procdef DROP CONSTRAINT act_re_procdef_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_re_model DROP CONSTRAINT act_re_model_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_re_deployment DROP CONSTRAINT act_re_deployment_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_procdef_info DROP CONSTRAINT act_procdef_info_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_id_user DROP CONSTRAINT act_id_user_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_id_token DROP CONSTRAINT act_id_token_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_id_property DROP CONSTRAINT act_id_property_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_id_priv DROP CONSTRAINT act_id_priv_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_id_priv_mapping DROP CONSTRAINT act_id_priv_mapping_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_id_membership DROP CONSTRAINT act_id_membership_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_id_info DROP CONSTRAINT act_id_info_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_id_group DROP CONSTRAINT act_id_group_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_id_bytearray DROP CONSTRAINT act_id_bytearray_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_hi_varinst DROP CONSTRAINT act_hi_varinst_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_hi_tsk_log DROP CONSTRAINT act_hi_tsk_log_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_hi_taskinst DROP CONSTRAINT act_hi_taskinst_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_hi_procinst DROP CONSTRAINT act_hi_procinst_proc_inst_id__key;
ALTER TABLE ONLY basicdb_flowable8.act_hi_procinst DROP CONSTRAINT act_hi_procinst_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_hi_identitylink DROP CONSTRAINT act_hi_identitylink_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_hi_entitylink DROP CONSTRAINT act_hi_entitylink_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_hi_detail DROP CONSTRAINT act_hi_detail_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_hi_comment DROP CONSTRAINT act_hi_comment_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_hi_attachment DROP CONSTRAINT act_hi_attachment_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_hi_actinst DROP CONSTRAINT act_hi_actinst_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ge_property DROP CONSTRAINT act_ge_property_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_ge_bytearray DROP CONSTRAINT act_ge_bytearray_pkey;
ALTER TABLE ONLY basicdb_flowable8.act_evt_log DROP CONSTRAINT act_evt_log_pkey;
ALTER TABLE ONLY basicdb_flowable8.sys_user_role DROP CONSTRAINT "PRIMARY00028";
ALTER TABLE ONLY basicdb_flowable8.sys_user_post DROP CONSTRAINT "PRIMARY00027";
ALTER TABLE ONLY basicdb_flowable8.sys_user DROP CONSTRAINT "PRIMARY00026";
ALTER TABLE ONLY basicdb_flowable8.sys_role_menu DROP CONSTRAINT "PRIMARY00025";
ALTER TABLE ONLY basicdb_flowable8.sys_role_dept DROP CONSTRAINT "PRIMARY00024";
ALTER TABLE ONLY basicdb_flowable8.sys_role DROP CONSTRAINT "PRIMARY00023";
ALTER TABLE ONLY basicdb_flowable8.sys_post DROP CONSTRAINT "PRIMARY00022";
ALTER TABLE ONLY basicdb_flowable8.sys_oper_log DROP CONSTRAINT "PRIMARY00021";
ALTER TABLE ONLY basicdb_flowable8.sys_notice DROP CONSTRAINT "PRIMARY00020";
ALTER TABLE ONLY basicdb_flowable8.sys_menu DROP CONSTRAINT "PRIMARY00019";
ALTER TABLE ONLY basicdb_flowable8.sys_logininfor DROP CONSTRAINT "PRIMARY00018";
ALTER TABLE ONLY basicdb_flowable8.sys_job_log DROP CONSTRAINT "PRIMARY00017";
ALTER TABLE ONLY basicdb_flowable8.sys_job DROP CONSTRAINT "PRIMARY00016";
ALTER TABLE ONLY basicdb_flowable8.sys_dict_type DROP CONSTRAINT "PRIMARY00015";
ALTER TABLE ONLY basicdb_flowable8.sys_dict_data DROP CONSTRAINT "PRIMARY00014";
ALTER TABLE ONLY basicdb_flowable8.sys_dept DROP CONSTRAINT "PRIMARY00013";
ALTER TABLE ONLY basicdb_flowable8.sys_config DROP CONSTRAINT "PRIMARY00012";
ALTER TABLE ONLY basicdb_flowable8.qrtz_triggers DROP CONSTRAINT "PRIMARY00011";
ALTER TABLE ONLY basicdb_flowable8.qrtz_simprop_triggers DROP CONSTRAINT "PRIMARY00010";
ALTER TABLE ONLY basicdb_flowable8.qrtz_simple_triggers DROP CONSTRAINT "PRIMARY00009";
ALTER TABLE ONLY basicdb_flowable8.qrtz_scheduler_state DROP CONSTRAINT "PRIMARY00008";
ALTER TABLE ONLY basicdb_flowable8.qrtz_paused_trigger_grps DROP CONSTRAINT "PRIMARY00007";
ALTER TABLE ONLY basicdb_flowable8.qrtz_locks DROP CONSTRAINT "PRIMARY00006";
ALTER TABLE ONLY basicdb_flowable8.qrtz_job_details DROP CONSTRAINT "PRIMARY00005";
ALTER TABLE ONLY basicdb_flowable8.qrtz_fired_triggers DROP CONSTRAINT "PRIMARY00004";
ALTER TABLE ONLY basicdb_flowable8.qrtz_cron_triggers DROP CONSTRAINT "PRIMARY00003";
ALTER TABLE ONLY basicdb_flowable8.qrtz_calendars DROP CONSTRAINT "PRIMARY00002";
ALTER TABLE ONLY basicdb_flowable8.qrtz_blob_triggers DROP CONSTRAINT "PRIMARY00001";
ALTER TABLE ONLY basicdb_flowable8.gen_table_column DROP CONSTRAINT "PRIMARY00000";
ALTER TABLE ONLY basicdb_flowable8.gen_table DROP CONSTRAINT "PRIMARY";
ALTER TABLE ONLY basicdb_flowable8.flw_event_resource DROP CONSTRAINT "FLW_EVENT_RESOURCE_pkey";
ALTER TABLE ONLY basicdb_flowable8.flw_event_deployment DROP CONSTRAINT "FLW_EVENT_DEPLOYMENT_pkey";
ALTER TABLE ONLY basicdb_flowable8.flw_event_definition DROP CONSTRAINT "FLW_EVENT_DEFINITION_pkey";
ALTER TABLE ONLY basicdb_flowable8.flw_channel_definition DROP CONSTRAINT "FLW_CHANNEL_DEFINITION_pkey";
ALTER TABLE basicdb_flowable8.sys_user ALTER COLUMN user_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.sys_role ALTER COLUMN role_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.sys_post ALTER COLUMN post_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.sys_oper_log ALTER COLUMN oper_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.sys_notice ALTER COLUMN notice_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.sys_menu ALTER COLUMN menu_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.sys_logininfor ALTER COLUMN info_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.sys_job_log ALTER COLUMN job_log_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.sys_job ALTER COLUMN job_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.sys_dict_type ALTER COLUMN dict_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.sys_dict_data ALTER COLUMN dict_code DROP DEFAULT;
ALTER TABLE basicdb_flowable8.sys_dept ALTER COLUMN dept_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.sys_config ALTER COLUMN config_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.gen_table_column ALTER COLUMN column_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.gen_table ALTER COLUMN table_id DROP DEFAULT;
ALTER TABLE basicdb_flowable8.act_hi_tsk_log ALTER COLUMN id_ DROP DEFAULT;
ALTER TABLE basicdb_flowable8.act_evt_log ALTER COLUMN log_nr_ DROP DEFAULT;
DROP TABLE basicdb_flowable8.wf_task_form;
DROP TABLE basicdb_flowable8.wf_form_template;
DROP TABLE basicdb_flowable8.wf_form_category;
DROP TABLE basicdb_flowable8.wf_form;
DROP TABLE basicdb_flowable8.wf_deploy_form;
DROP TABLE basicdb_flowable8.wf_copy;
DROP TABLE basicdb_flowable8.wf_comment_info;
DROP TABLE basicdb_flowable8.wf_category_org_link;
DROP TABLE basicdb_flowable8.wf_category;
DROP TABLE basicdb_flowable8.tbl_flow_extend_procinst;
DROP SEQUENCE basicdb_flowable8.sys_user_user_id_seq;
DROP TABLE basicdb_flowable8.sys_user_role;
DROP TABLE basicdb_flowable8.sys_user_post;
DROP TABLE basicdb_flowable8.sys_user;
DROP SEQUENCE basicdb_flowable8.sys_role_role_id_seq;
DROP TABLE basicdb_flowable8.sys_role_menu;
DROP TABLE basicdb_flowable8.sys_role_dept;
DROP TABLE basicdb_flowable8.sys_role;
DROP SEQUENCE basicdb_flowable8.sys_post_post_id_seq;
DROP TABLE basicdb_flowable8.sys_post;
DROP SEQUENCE basicdb_flowable8.sys_oper_log_oper_id_seq;
DROP TABLE basicdb_flowable8.sys_oper_log;
DROP SEQUENCE basicdb_flowable8.sys_notice_read_read_id_seq;
DROP TABLE basicdb_flowable8.sys_notice_read;
DROP SEQUENCE basicdb_flowable8.sys_notice_notice_id_seq;
DROP TABLE basicdb_flowable8.sys_notice;
DROP SEQUENCE basicdb_flowable8.sys_menu_menu_id_seq;
DROP TABLE basicdb_flowable8.sys_menu;
DROP SEQUENCE basicdb_flowable8.sys_logininfor_info_id_seq;
DROP TABLE basicdb_flowable8.sys_logininfor;
DROP SEQUENCE basicdb_flowable8.sys_job_log_job_log_id_seq;
DROP TABLE basicdb_flowable8.sys_job_log;
DROP SEQUENCE basicdb_flowable8.sys_job_job_id_seq;
DROP TABLE basicdb_flowable8.sys_job;
DROP TABLE basicdb_flowable8.sys_i18n;
DROP TABLE basicdb_flowable8.sys_files;
DROP SEQUENCE basicdb_flowable8.sys_dict_type_dict_id_seq;
DROP TABLE basicdb_flowable8.sys_dict_type;
DROP SEQUENCE basicdb_flowable8.sys_dict_data_dict_code_seq;
DROP TABLE basicdb_flowable8.sys_dict_data;
DROP SEQUENCE basicdb_flowable8.sys_dept_dept_id_seq;
DROP TABLE basicdb_flowable8.sys_dept;
DROP TABLE basicdb_flowable8.sys_demo;
DROP SEQUENCE basicdb_flowable8.sys_config_config_id_seq;
DROP TABLE basicdb_flowable8.sys_config;
DROP TABLE basicdb_flowable8.qrtz_triggers;
DROP TABLE basicdb_flowable8.qrtz_simprop_triggers;
DROP TABLE basicdb_flowable8.qrtz_simple_triggers;
DROP TABLE basicdb_flowable8.qrtz_scheduler_state;
DROP TABLE basicdb_flowable8.qrtz_paused_trigger_grps;
DROP TABLE basicdb_flowable8.qrtz_locks;
DROP TABLE basicdb_flowable8.qrtz_job_details;
DROP TABLE basicdb_flowable8.qrtz_fired_triggers;
DROP TABLE basicdb_flowable8.qrtz_cron_triggers;
DROP TABLE basicdb_flowable8.qrtz_calendars;
DROP TABLE basicdb_flowable8.qrtz_blob_triggers;
DROP VIEW basicdb_flowable8.list_table;
DROP VIEW basicdb_flowable8.list_column;
DROP SEQUENCE basicdb_flowable8.gen_table_table_id_seq;
DROP SEQUENCE basicdb_flowable8.gen_table_column_column_id_seq;
DROP TABLE basicdb_flowable8.gen_table_column;
DROP TABLE basicdb_flowable8.gen_table;
DROP TABLE basicdb_flowable8.flw_ru_batch_part;
DROP TABLE basicdb_flowable8.flw_ru_batch;
DROP TABLE basicdb_flowable8.flw_expression;
DROP TABLE basicdb_flowable8.flw_event_resource;
DROP TABLE basicdb_flowable8.flw_event_deployment;
DROP TABLE basicdb_flowable8.flw_event_definition;
DROP TABLE basicdb_flowable8.flw_channel_definition;
DROP TABLE basicdb_flowable8.act_ru_variable;
DROP TABLE basicdb_flowable8.act_ru_timer_job;
DROP TABLE basicdb_flowable8.act_ru_task;
DROP TABLE basicdb_flowable8.act_ru_suspended_job;
DROP TABLE basicdb_flowable8.act_ru_job;
DROP TABLE basicdb_flowable8.act_ru_identitylink;
DROP TABLE basicdb_flowable8.act_ru_history_job;
DROP TABLE basicdb_flowable8.act_ru_external_job;
DROP TABLE basicdb_flowable8.act_ru_execution;
DROP TABLE basicdb_flowable8.act_ru_event_subscr;
DROP TABLE basicdb_flowable8.act_ru_entitylink;
DROP TABLE basicdb_flowable8.act_ru_deadletter_job;
DROP TABLE basicdb_flowable8.act_ru_actinst;
DROP TABLE basicdb_flowable8.act_re_procdef;
DROP TABLE basicdb_flowable8.act_re_model;
DROP TABLE basicdb_flowable8.act_re_deployment;
DROP TABLE basicdb_flowable8.act_procdef_info;
DROP TABLE basicdb_flowable8.act_id_user;
DROP TABLE basicdb_flowable8.act_id_token;
DROP TABLE basicdb_flowable8.act_id_property;
DROP TABLE basicdb_flowable8.act_id_priv_mapping;
DROP TABLE basicdb_flowable8.act_id_priv;
DROP TABLE basicdb_flowable8.act_id_membership;
DROP TABLE basicdb_flowable8.act_id_info;
DROP TABLE basicdb_flowable8.act_id_group;
DROP TABLE basicdb_flowable8.act_id_bytearray;
DROP TABLE basicdb_flowable8.act_hi_varinst;
DROP SEQUENCE basicdb_flowable8.act_hi_tsk_log_id__seq;
DROP TABLE basicdb_flowable8.act_hi_tsk_log;
DROP TABLE basicdb_flowable8.act_hi_taskinst;
DROP TABLE basicdb_flowable8.act_hi_procinst;
DROP TABLE basicdb_flowable8.act_hi_identitylink;
DROP TABLE basicdb_flowable8.act_hi_entitylink;
DROP TABLE basicdb_flowable8.act_hi_detail;
DROP TABLE basicdb_flowable8.act_hi_comment;
DROP TABLE basicdb_flowable8.act_hi_attachment;
DROP TABLE basicdb_flowable8.act_hi_actinst;
DROP TABLE basicdb_flowable8.act_ge_property;
DROP TABLE basicdb_flowable8.act_ge_bytearray;
DROP SEQUENCE basicdb_flowable8.act_evt_log_log_nr__seq;
DROP TABLE basicdb_flowable8.act_evt_log;
DROP FUNCTION basicdb_flowable8.tabledef(oid);
DROP FUNCTION basicdb_flowable8.ifnull(param1 text, param2 text);
DROP FUNCTION basicdb_flowable8.find_in_set(bigint, character varying);
DROP FUNCTION basicdb_flowable8.exec(sqlstring character varying);
DROP FUNCTION basicdb_flowable8.date_format(indate anyelement, informate text);
DROP SCHEMA basicdb_flowable8;
--
-- TOC entry 10 (class 2615 OID 4489664)
-- Name: basicdb_flowable8; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA basicdb_flowable8;


ALTER SCHEMA basicdb_flowable8 OWNER TO postgres;

--
-- TOC entry 509 (class 1255 OID 4489673)
-- Name: date_format(anyelement, text); Type: FUNCTION; Schema: basicdb_flowable8; Owner: system
--

CREATE FUNCTION basicdb_flowable8.date_format(indate anyelement, informate text) RETURNS text
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


ALTER FUNCTION basicdb_flowable8.date_format(indate anyelement, informate text) OWNER TO system;

--
-- TOC entry 510 (class 1255 OID 4489674)
-- Name: exec(character varying); Type: FUNCTION; Schema: basicdb_flowable8; Owner: system
--

CREATE FUNCTION basicdb_flowable8.exec(sqlstring character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
declare
res varchar(50);
BEGIN
EXECUTE sqlstring;
RETURN 'ok';
END
$$;


ALTER FUNCTION basicdb_flowable8.exec(sqlstring character varying) OWNER TO system;

--
-- TOC entry 511 (class 1255 OID 4489675)
-- Name: find_in_set(bigint, character varying); Type: FUNCTION; Schema: basicdb_flowable8; Owner: system
--

CREATE FUNCTION basicdb_flowable8.find_in_set(bigint, character varying) RETURNS boolean
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


ALTER FUNCTION basicdb_flowable8.find_in_set(bigint, character varying) OWNER TO system;

--
-- TOC entry 507 (class 1255 OID 4489676)
-- Name: ifnull(text, text); Type: FUNCTION; Schema: basicdb_flowable8; Owner: system
--

CREATE FUNCTION basicdb_flowable8.ifnull(param1 text, param2 text) RETURNS text
    LANGUAGE plpgsql
    AS $_$
BEGIN
    return COALESCE($1,$2);
END;
$_$;


ALTER FUNCTION basicdb_flowable8.ifnull(param1 text, param2 text) OWNER TO system;

--
-- TOC entry 512 (class 1255 OID 4489677)
-- Name: tabledef(oid); Type: FUNCTION; Schema: basicdb_flowable8; Owner: system
--

CREATE FUNCTION basicdb_flowable8.tabledef(oid) RETURNS text
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


ALTER FUNCTION basicdb_flowable8.tabledef(oid) OWNER TO system;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 455 (class 1259 OID 4490307)
-- Name: act_evt_log; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_evt_log (
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


ALTER TABLE basicdb_flowable8.act_evt_log OWNER TO system;

--
-- TOC entry 454 (class 1259 OID 4490306)
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: system
--

CREATE SEQUENCE basicdb_flowable8.act_evt_log_log_nr__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.act_evt_log_log_nr__seq OWNER TO system;

--
-- TOC entry 4821 (class 0 OID 0)
-- Dependencies: 454
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: system
--

ALTER SEQUENCE basicdb_flowable8.act_evt_log_log_nr__seq OWNED BY basicdb_flowable8.act_evt_log.log_nr_;


--
-- TOC entry 430 (class 1259 OID 4489997)
-- Name: act_ge_bytearray; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ge_bytearray (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    deployment_id_ character varying(64),
    bytes_ bytea,
    generated_ boolean
);


ALTER TABLE basicdb_flowable8.act_ge_bytearray OWNER TO system;

--
-- TOC entry 429 (class 1259 OID 4489992)
-- Name: act_ge_property; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ge_property (
    name_ character varying(64) NOT NULL,
    value_ character varying(300),
    rev_ integer
);


ALTER TABLE basicdb_flowable8.act_ge_property OWNER TO system;

--
-- TOC entry 459 (class 1259 OID 4490541)
-- Name: act_hi_actinst; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_hi_actinst (
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


ALTER TABLE basicdb_flowable8.act_hi_actinst OWNER TO system;

--
-- TOC entry 462 (class 1259 OID 4490564)
-- Name: act_hi_attachment; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_hi_attachment (
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


ALTER TABLE basicdb_flowable8.act_hi_attachment OWNER TO system;

--
-- TOC entry 461 (class 1259 OID 4490557)
-- Name: act_hi_comment; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_hi_comment (
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


ALTER TABLE basicdb_flowable8.act_hi_comment OWNER TO system;

--
-- TOC entry 460 (class 1259 OID 4490550)
-- Name: act_hi_detail; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_hi_detail (
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


ALTER TABLE basicdb_flowable8.act_hi_detail OWNER TO system;

--
-- TOC entry 432 (class 1259 OID 4490015)
-- Name: act_hi_entitylink; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_hi_entitylink (
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


ALTER TABLE basicdb_flowable8.act_hi_entitylink OWNER TO system;

--
-- TOC entry 434 (class 1259 OID 4490038)
-- Name: act_hi_identitylink; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_hi_identitylink (
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


ALTER TABLE basicdb_flowable8.act_hi_identitylink OWNER TO system;

--
-- TOC entry 458 (class 1259 OID 4490530)
-- Name: act_hi_procinst; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_hi_procinst (
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
    business_status_ character varying(255),
    end_user_id_ character varying(255),
    state_ character varying(255)
);


ALTER TABLE basicdb_flowable8.act_hi_procinst OWNER TO system;

--
-- TOC entry 444 (class 1259 OID 4490212)
-- Name: act_hi_taskinst; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_hi_taskinst (
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


ALTER TABLE basicdb_flowable8.act_hi_taskinst OWNER TO system;

--
-- TOC entry 446 (class 1259 OID 4490222)
-- Name: act_hi_tsk_log; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_hi_tsk_log (
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


ALTER TABLE basicdb_flowable8.act_hi_tsk_log OWNER TO system;

--
-- TOC entry 445 (class 1259 OID 4490221)
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: system
--

CREATE SEQUENCE basicdb_flowable8.act_hi_tsk_log_id__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.act_hi_tsk_log_id__seq OWNER TO system;

--
-- TOC entry 4822 (class 0 OID 0)
-- Dependencies: 445
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: system
--

ALTER SEQUENCE basicdb_flowable8.act_hi_tsk_log_id__seq OWNED BY basicdb_flowable8.act_hi_tsk_log.id_;


--
-- TOC entry 448 (class 1259 OID 4490250)
-- Name: act_hi_varinst; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_hi_varinst (
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


ALTER TABLE basicdb_flowable8.act_hi_varinst OWNER TO system;

--
-- TOC entry 464 (class 1259 OID 4490595)
-- Name: act_id_bytearray; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_id_bytearray (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    bytes_ bytea
);


ALTER TABLE basicdb_flowable8.act_id_bytearray OWNER TO system;

--
-- TOC entry 465 (class 1259 OID 4490602)
-- Name: act_id_group; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_id_group (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    type_ character varying(255)
);


ALTER TABLE basicdb_flowable8.act_id_group OWNER TO system;

--
-- TOC entry 468 (class 1259 OID 4490622)
-- Name: act_id_info; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_id_info (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    user_id_ character varying(64),
    type_ character varying(64),
    key_ character varying(255),
    value_ character varying(255),
    password_ bytea,
    parent_id_ character varying(255)
);


ALTER TABLE basicdb_flowable8.act_id_info OWNER TO system;

--
-- TOC entry 466 (class 1259 OID 4490609)
-- Name: act_id_membership; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_id_membership (
    user_id_ character varying(64) NOT NULL,
    group_id_ character varying(64) NOT NULL
);


ALTER TABLE basicdb_flowable8.act_id_membership OWNER TO system;

--
-- TOC entry 470 (class 1259 OID 4490636)
-- Name: act_id_priv; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_id_priv (
    id_ character varying(64) NOT NULL,
    name_ character varying(255) NOT NULL
);


ALTER TABLE basicdb_flowable8.act_id_priv OWNER TO system;

--
-- TOC entry 471 (class 1259 OID 4490641)
-- Name: act_id_priv_mapping; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_id_priv_mapping (
    id_ character varying(64) NOT NULL,
    priv_id_ character varying(64) NOT NULL,
    user_id_ character varying(255),
    group_id_ character varying(255)
);


ALTER TABLE basicdb_flowable8.act_id_priv_mapping OWNER TO system;

--
-- TOC entry 463 (class 1259 OID 4490590)
-- Name: act_id_property; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_id_property (
    name_ character varying(64) NOT NULL,
    value_ character varying(300),
    rev_ integer
);


ALTER TABLE basicdb_flowable8.act_id_property OWNER TO system;

--
-- TOC entry 469 (class 1259 OID 4490629)
-- Name: act_id_token; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_id_token (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    token_value_ character varying(255),
    token_date_ timestamp without time zone,
    ip_address_ character varying(255),
    user_agent_ character varying(255),
    user_id_ character varying(255),
    token_data_ character varying(2000)
);


ALTER TABLE basicdb_flowable8.act_id_token OWNER TO system;

--
-- TOC entry 467 (class 1259 OID 4490614)
-- Name: act_id_user; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_id_user (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    first_ character varying(255),
    last_ character varying(255),
    display_name_ character varying(255),
    email_ character varying(255),
    pwd_ character varying(255),
    picture_id_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


ALTER TABLE basicdb_flowable8.act_id_user OWNER TO system;

--
-- TOC entry 456 (class 1259 OID 4490316)
-- Name: act_procdef_info; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_procdef_info (
    id_ character varying(64) NOT NULL,
    proc_def_id_ character varying(64) NOT NULL,
    rev_ integer,
    info_json_id_ character varying(64)
);


ALTER TABLE basicdb_flowable8.act_procdef_info OWNER TO system;

--
-- TOC entry 450 (class 1259 OID 4490273)
-- Name: act_re_deployment; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_re_deployment (
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


ALTER TABLE basicdb_flowable8.act_re_deployment OWNER TO system;

--
-- TOC entry 451 (class 1259 OID 4490281)
-- Name: act_re_model; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_re_model (
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


ALTER TABLE basicdb_flowable8.act_re_model OWNER TO system;

--
-- TOC entry 453 (class 1259 OID 4490297)
-- Name: act_re_procdef; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_re_procdef (
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


ALTER TABLE basicdb_flowable8.act_re_procdef OWNER TO system;

--
-- TOC entry 457 (class 1259 OID 4490321)
-- Name: act_ru_actinst; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_actinst (
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


ALTER TABLE basicdb_flowable8.act_ru_actinst OWNER TO system;

--
-- TOC entry 438 (class 1259 OID 4490073)
-- Name: act_ru_deadletter_job; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_deadletter_job (
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


ALTER TABLE basicdb_flowable8.act_ru_deadletter_job OWNER TO system;

--
-- TOC entry 431 (class 1259 OID 4490004)
-- Name: act_ru_entitylink; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_entitylink (
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


ALTER TABLE basicdb_flowable8.act_ru_entitylink OWNER TO system;

--
-- TOC entry 449 (class 1259 OID 4490261)
-- Name: act_ru_event_subscr; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_event_subscr (
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


ALTER TABLE basicdb_flowable8.act_ru_event_subscr OWNER TO system;

--
-- TOC entry 452 (class 1259 OID 4490289)
-- Name: act_ru_execution; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_execution (
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


ALTER TABLE basicdb_flowable8.act_ru_execution OWNER TO system;

--
-- TOC entry 440 (class 1259 OID 4490089)
-- Name: act_ru_external_job; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_external_job (
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


ALTER TABLE basicdb_flowable8.act_ru_external_job OWNER TO system;

--
-- TOC entry 439 (class 1259 OID 4490081)
-- Name: act_ru_history_job; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_history_job (
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


ALTER TABLE basicdb_flowable8.act_ru_history_job OWNER TO system;

--
-- TOC entry 433 (class 1259 OID 4490026)
-- Name: act_ru_identitylink; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_identitylink (
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


ALTER TABLE basicdb_flowable8.act_ru_identitylink OWNER TO system;

--
-- TOC entry 435 (class 1259 OID 4490049)
-- Name: act_ru_job; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_job (
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


ALTER TABLE basicdb_flowable8.act_ru_job OWNER TO system;

--
-- TOC entry 437 (class 1259 OID 4490065)
-- Name: act_ru_suspended_job; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_suspended_job (
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


ALTER TABLE basicdb_flowable8.act_ru_suspended_job OWNER TO system;

--
-- TOC entry 443 (class 1259 OID 4490200)
-- Name: act_ru_task; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_task (
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


ALTER TABLE basicdb_flowable8.act_ru_task OWNER TO system;

--
-- TOC entry 436 (class 1259 OID 4490057)
-- Name: act_ru_timer_job; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_timer_job (
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


ALTER TABLE basicdb_flowable8.act_ru_timer_job OWNER TO system;

--
-- TOC entry 447 (class 1259 OID 4490235)
-- Name: act_ru_variable; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.act_ru_variable (
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


ALTER TABLE basicdb_flowable8.act_ru_variable OWNER TO system;

--
-- TOC entry 475 (class 1259 OID 4490698)
-- Name: flw_channel_definition; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.flw_channel_definition (
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


ALTER TABLE basicdb_flowable8.flw_channel_definition OWNER TO system;

--
-- TOC entry 474 (class 1259 OID 4490684)
-- Name: flw_event_definition; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.flw_event_definition (
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


ALTER TABLE basicdb_flowable8.flw_event_definition OWNER TO system;

--
-- TOC entry 472 (class 1259 OID 4490670)
-- Name: flw_event_deployment; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.flw_event_deployment (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    category_ character varying(255),
    deploy_time_ timestamp(3) without time zone,
    tenant_id_ character varying(255),
    parent_deployment_id_ character varying(255)
);


ALTER TABLE basicdb_flowable8.flw_event_deployment OWNER TO system;

--
-- TOC entry 473 (class 1259 OID 4490677)
-- Name: flw_event_resource; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.flw_event_resource (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    deployment_id_ character varying(255),
    resource_bytes_ bytea
);


ALTER TABLE basicdb_flowable8.flw_event_resource OWNER TO system;

--
-- TOC entry 486 (class 1259 OID 4499741)
-- Name: flw_expression; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.flw_expression (
    id bigint NOT NULL,
    name character varying(100) DEFAULT NULL::character varying,
    expression character varying(500) DEFAULT NULL::character varying,
    status integer DEFAULT 0,
    type character varying(20) DEFAULT NULL::character varying,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE basicdb_flowable8.flw_expression OWNER TO system;

--
-- TOC entry 441 (class 1259 OID 4490178)
-- Name: flw_ru_batch; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.flw_ru_batch (
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


ALTER TABLE basicdb_flowable8.flw_ru_batch OWNER TO system;

--
-- TOC entry 442 (class 1259 OID 4490186)
-- Name: flw_ru_batch_part; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.flw_ru_batch_part (
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


ALTER TABLE basicdb_flowable8.flw_ru_batch_part OWNER TO system;

--
-- TOC entry 377 (class 1259 OID 4489678)
-- Name: gen_table; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.gen_table (
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


ALTER TABLE basicdb_flowable8.gen_table OWNER TO system;

--
-- TOC entry 378 (class 1259 OID 4489686)
-- Name: gen_table_column; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.gen_table_column (
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


ALTER TABLE basicdb_flowable8.gen_table_column OWNER TO postgres;

--
-- TOC entry 379 (class 1259 OID 4489692)
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.gen_table_column_column_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.gen_table_column_column_id_seq OWNER TO postgres;

--
-- TOC entry 4825 (class 0 OID 0)
-- Dependencies: 379
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.gen_table_column_column_id_seq OWNED BY basicdb_flowable8.gen_table_column.column_id;


--
-- TOC entry 380 (class 1259 OID 4489693)
-- Name: gen_table_table_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: system
--

CREATE SEQUENCE basicdb_flowable8.gen_table_table_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.gen_table_table_id_seq OWNER TO system;

--
-- TOC entry 4827 (class 0 OID 0)
-- Dependencies: 380
-- Name: gen_table_table_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: system
--

ALTER SEQUENCE basicdb_flowable8.gen_table_table_id_seq OWNED BY basicdb_flowable8.gen_table.table_id;


--
-- TOC entry 381 (class 1259 OID 4489694)
-- Name: list_column; Type: VIEW; Schema: basicdb_flowable8; Owner: system
--

CREATE VIEW basicdb_flowable8.list_column AS
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
  WHERE ((c.relkind = ANY (ARRAY['r'::"char", 'p'::"char"])) AND (a.attnum > 0) AND (n.nspname = 'basicdb_flowable8'::name))
  ORDER BY c.relname, a.attnum;


ALTER VIEW basicdb_flowable8.list_column OWNER TO system;

--
-- TOC entry 382 (class 1259 OID 4489699)
-- Name: list_table; Type: VIEW; Schema: basicdb_flowable8; Owner: system
--

CREATE VIEW basicdb_flowable8.list_table AS
 SELECT c.relname AS table_name,
    obj_description(c.oid) AS table_comment,
    CURRENT_TIMESTAMP AS create_time,
    CURRENT_TIMESTAMP AS update_time
   FROM (pg_class c
     LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace)))
  WHERE ((c.relkind = ANY (ARRAY['r'::"char", 'p'::"char"])) AND (c.relname !~~ 'spatial_%'::text) AND (n.nspname = 'basicdb_flowable8'::name) AND (n.nspname <> ''::name));


ALTER VIEW basicdb_flowable8.list_table OWNER TO system;

--
-- TOC entry 383 (class 1259 OID 4489704)
-- Name: qrtz_blob_triggers; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.qrtz_blob_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea
);


ALTER TABLE basicdb_flowable8.qrtz_blob_triggers OWNER TO postgres;

--
-- TOC entry 384 (class 1259 OID 4489709)
-- Name: qrtz_calendars; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.qrtz_calendars (
    sched_name character varying(120) NOT NULL,
    calendar_name character varying(200) NOT NULL,
    calendar bytea NOT NULL
);


ALTER TABLE basicdb_flowable8.qrtz_calendars OWNER TO postgres;

--
-- TOC entry 385 (class 1259 OID 4489714)
-- Name: qrtz_cron_triggers; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.qrtz_cron_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(200) NOT NULL,
    time_zone_id character varying(80)
);


ALTER TABLE basicdb_flowable8.qrtz_cron_triggers OWNER TO postgres;

--
-- TOC entry 386 (class 1259 OID 4489719)
-- Name: qrtz_fired_triggers; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.qrtz_fired_triggers (
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


ALTER TABLE basicdb_flowable8.qrtz_fired_triggers OWNER TO postgres;

--
-- TOC entry 387 (class 1259 OID 4489724)
-- Name: qrtz_job_details; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.qrtz_job_details (
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


ALTER TABLE basicdb_flowable8.qrtz_job_details OWNER TO postgres;

--
-- TOC entry 388 (class 1259 OID 4489729)
-- Name: qrtz_locks; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.qrtz_locks (
    sched_name character varying(120) NOT NULL,
    lock_name character varying(40) NOT NULL
);


ALTER TABLE basicdb_flowable8.qrtz_locks OWNER TO postgres;

--
-- TOC entry 389 (class 1259 OID 4489732)
-- Name: qrtz_paused_trigger_grps; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.qrtz_paused_trigger_grps (
    sched_name character varying(120) NOT NULL,
    trigger_group character varying(200) NOT NULL
);


ALTER TABLE basicdb_flowable8.qrtz_paused_trigger_grps OWNER TO postgres;

--
-- TOC entry 390 (class 1259 OID 4489735)
-- Name: qrtz_scheduler_state; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.qrtz_scheduler_state (
    sched_name character varying(120) NOT NULL,
    instance_name character varying(200) NOT NULL,
    last_checkin_time bigint NOT NULL,
    checkin_interval bigint NOT NULL
);


ALTER TABLE basicdb_flowable8.qrtz_scheduler_state OWNER TO postgres;

--
-- TOC entry 391 (class 1259 OID 4489738)
-- Name: qrtz_simple_triggers; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.qrtz_simple_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count bigint NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL
);


ALTER TABLE basicdb_flowable8.qrtz_simple_triggers OWNER TO postgres;

--
-- TOC entry 392 (class 1259 OID 4489743)
-- Name: qrtz_simprop_triggers; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.qrtz_simprop_triggers (
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


ALTER TABLE basicdb_flowable8.qrtz_simprop_triggers OWNER TO postgres;

--
-- TOC entry 393 (class 1259 OID 4489748)
-- Name: qrtz_triggers; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.qrtz_triggers (
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


ALTER TABLE basicdb_flowable8.qrtz_triggers OWNER TO postgres;

--
-- TOC entry 394 (class 1259 OID 4489753)
-- Name: sys_config; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_config (
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


ALTER TABLE basicdb_flowable8.sys_config OWNER TO postgres;

--
-- TOC entry 395 (class 1259 OID 4489759)
-- Name: sys_config_config_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_config_config_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_config_config_id_seq OWNER TO postgres;

--
-- TOC entry 4843 (class 0 OID 0)
-- Dependencies: 395
-- Name: sys_config_config_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_config_config_id_seq OWNED BY basicdb_flowable8.sys_config.config_id;


--
-- TOC entry 396 (class 1259 OID 4489760)
-- Name: sys_demo; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.sys_demo (
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


ALTER TABLE basicdb_flowable8.sys_demo OWNER TO system;

--
-- TOC entry 397 (class 1259 OID 4489763)
-- Name: sys_dept; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_dept (
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


ALTER TABLE basicdb_flowable8.sys_dept OWNER TO postgres;

--
-- TOC entry 398 (class 1259 OID 4489772)
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_dept_dept_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_dept_dept_id_seq OWNER TO postgres;

--
-- TOC entry 4846 (class 0 OID 0)
-- Dependencies: 398
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_dept_dept_id_seq OWNED BY basicdb_flowable8.sys_dept.dept_id;


--
-- TOC entry 399 (class 1259 OID 4489773)
-- Name: sys_dict_data; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_dict_data (
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


ALTER TABLE basicdb_flowable8.sys_dict_data OWNER TO postgres;

--
-- TOC entry 400 (class 1259 OID 4489781)
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_dict_data_dict_code_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_dict_data_dict_code_seq OWNER TO postgres;

--
-- TOC entry 4849 (class 0 OID 0)
-- Dependencies: 400
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_dict_data_dict_code_seq OWNED BY basicdb_flowable8.sys_dict_data.dict_code;


--
-- TOC entry 401 (class 1259 OID 4489782)
-- Name: sys_dict_type; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_dict_type (
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


ALTER TABLE basicdb_flowable8.sys_dict_type OWNER TO postgres;

--
-- TOC entry 402 (class 1259 OID 4489788)
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_dict_type_dict_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_dict_type_dict_id_seq OWNER TO postgres;

--
-- TOC entry 4852 (class 0 OID 0)
-- Dependencies: 402
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_dict_type_dict_id_seq OWNED BY basicdb_flowable8.sys_dict_type.dict_id;


--
-- TOC entry 403 (class 1259 OID 4489789)
-- Name: sys_files; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.sys_files (
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


ALTER TABLE basicdb_flowable8.sys_files OWNER TO system;

--
-- TOC entry 404 (class 1259 OID 4489794)
-- Name: sys_i18n; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.sys_i18n (
    message_id character varying(255) NOT NULL,
    value text NOT NULL,
    language character varying(50) NOT NULL
);


ALTER TABLE basicdb_flowable8.sys_i18n OWNER TO system;

--
-- TOC entry 4854 (class 0 OID 0)
-- Dependencies: 404
-- Name: COLUMN sys_i18n.message_id; Type: COMMENT; Schema: basicdb_flowable8; Owner: system
--

COMMENT ON COLUMN basicdb_flowable8.sys_i18n.message_id IS '标识';


--
-- TOC entry 4855 (class 0 OID 0)
-- Dependencies: 404
-- Name: COLUMN sys_i18n.value; Type: COMMENT; Schema: basicdb_flowable8; Owner: system
--

COMMENT ON COLUMN basicdb_flowable8.sys_i18n.value IS '值';


--
-- TOC entry 4856 (class 0 OID 0)
-- Dependencies: 404
-- Name: COLUMN sys_i18n.language; Type: COMMENT; Schema: basicdb_flowable8; Owner: system
--

COMMENT ON COLUMN basicdb_flowable8.sys_i18n.language IS '语言';


--
-- TOC entry 405 (class 1259 OID 4489799)
-- Name: sys_job; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_job (
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


ALTER TABLE basicdb_flowable8.sys_job OWNER TO postgres;

--
-- TOC entry 406 (class 1259 OID 4489808)
-- Name: sys_job_job_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_job_job_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_job_job_id_seq OWNER TO postgres;

--
-- TOC entry 4858 (class 0 OID 0)
-- Dependencies: 406
-- Name: sys_job_job_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_job_job_id_seq OWNED BY basicdb_flowable8.sys_job.job_id;


--
-- TOC entry 407 (class 1259 OID 4489809)
-- Name: sys_job_log; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_job_log (
    job_log_id bigint NOT NULL,
    job_name character varying(64) NOT NULL,
    job_group character varying(64) NOT NULL,
    invoke_target character varying(500) NOT NULL,
    job_message character varying(500),
    status character varying(3) DEFAULT '0'::character varying,
    exception_info character varying(2000),
    create_time timestamp(0) without time zone
);


ALTER TABLE basicdb_flowable8.sys_job_log OWNER TO postgres;

--
-- TOC entry 408 (class 1259 OID 4489815)
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_job_log_job_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_job_log_job_log_id_seq OWNER TO postgres;

--
-- TOC entry 4861 (class 0 OID 0)
-- Dependencies: 408
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_job_log_job_log_id_seq OWNED BY basicdb_flowable8.sys_job_log.job_log_id;


--
-- TOC entry 409 (class 1259 OID 4489816)
-- Name: sys_logininfor; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_logininfor (
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


ALTER TABLE basicdb_flowable8.sys_logininfor OWNER TO postgres;

--
-- TOC entry 410 (class 1259 OID 4489822)
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_logininfor_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_logininfor_info_id_seq OWNER TO postgres;

--
-- TOC entry 4864 (class 0 OID 0)
-- Dependencies: 410
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_logininfor_info_id_seq OWNED BY basicdb_flowable8.sys_logininfor.info_id;


--
-- TOC entry 411 (class 1259 OID 4489823)
-- Name: sys_menu; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_menu (
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


ALTER TABLE basicdb_flowable8.sys_menu OWNER TO postgres;

--
-- TOC entry 4866 (class 0 OID 0)
-- Dependencies: 411
-- Name: COLUMN sys_menu.show_tags; Type: COMMENT; Schema: basicdb_flowable8; Owner: postgres
--

COMMENT ON COLUMN basicdb_flowable8.sys_menu.show_tags IS '自定义配置该页面是否展示Tags-view';


--
-- TOC entry 412 (class 1259 OID 4489835)
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_menu_menu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_menu_menu_id_seq OWNER TO postgres;

--
-- TOC entry 4868 (class 0 OID 0)
-- Dependencies: 412
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_menu_menu_id_seq OWNED BY basicdb_flowable8.sys_menu.menu_id;


--
-- TOC entry 413 (class 1259 OID 4489836)
-- Name: sys_notice; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_notice (
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


ALTER TABLE basicdb_flowable8.sys_notice OWNER TO postgres;

--
-- TOC entry 414 (class 1259 OID 4489842)
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_notice_notice_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_notice_notice_id_seq OWNER TO postgres;

--
-- TOC entry 4871 (class 0 OID 0)
-- Dependencies: 414
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_notice_notice_id_seq OWNED BY basicdb_flowable8.sys_notice.notice_id;


--
-- TOC entry 415 (class 1259 OID 4489843)
-- Name: sys_notice_read; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.sys_notice_read (
    read_id bigint NOT NULL,
    notice_id bigint NOT NULL,
    user_id bigint NOT NULL,
    read_time timestamp(0) without time zone
);


ALTER TABLE basicdb_flowable8.sys_notice_read OWNER TO system;

--
-- TOC entry 416 (class 1259 OID 4489846)
-- Name: sys_notice_read_read_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: system
--

CREATE SEQUENCE basicdb_flowable8.sys_notice_read_read_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_notice_read_read_id_seq OWNER TO system;

--
-- TOC entry 4873 (class 0 OID 0)
-- Dependencies: 416
-- Name: sys_notice_read_read_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: system
--

ALTER SEQUENCE basicdb_flowable8.sys_notice_read_read_id_seq OWNED BY basicdb_flowable8.sys_notice_read.read_id;


--
-- TOC entry 417 (class 1259 OID 4489847)
-- Name: sys_oper_log; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_oper_log (
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


ALTER TABLE basicdb_flowable8.sys_oper_log OWNER TO postgres;

--
-- TOC entry 418 (class 1259 OID 4489855)
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_oper_log_oper_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_oper_log_oper_id_seq OWNER TO postgres;

--
-- TOC entry 4875 (class 0 OID 0)
-- Dependencies: 418
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_oper_log_oper_id_seq OWNED BY basicdb_flowable8.sys_oper_log.oper_id;


--
-- TOC entry 419 (class 1259 OID 4489856)
-- Name: sys_post; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_post (
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


ALTER TABLE basicdb_flowable8.sys_post OWNER TO postgres;

--
-- TOC entry 420 (class 1259 OID 4489861)
-- Name: sys_post_post_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_post_post_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_post_post_id_seq OWNER TO postgres;

--
-- TOC entry 4878 (class 0 OID 0)
-- Dependencies: 420
-- Name: sys_post_post_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_post_post_id_seq OWNED BY basicdb_flowable8.sys_post.post_id;


--
-- TOC entry 421 (class 1259 OID 4489862)
-- Name: sys_role; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_role (
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


ALTER TABLE basicdb_flowable8.sys_role OWNER TO postgres;

--
-- TOC entry 422 (class 1259 OID 4489871)
-- Name: sys_role_dept; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_role_dept (
    role_id bigint NOT NULL,
    dept_id bigint NOT NULL
);


ALTER TABLE basicdb_flowable8.sys_role_dept OWNER TO postgres;

--
-- TOC entry 423 (class 1259 OID 4489874)
-- Name: sys_role_menu; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_role_menu (
    role_id bigint NOT NULL,
    menu_id bigint NOT NULL
);


ALTER TABLE basicdb_flowable8.sys_role_menu OWNER TO postgres;

--
-- TOC entry 424 (class 1259 OID 4489877)
-- Name: sys_role_role_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_role_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_role_role_id_seq OWNER TO postgres;

--
-- TOC entry 4883 (class 0 OID 0)
-- Dependencies: 424
-- Name: sys_role_role_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_role_role_id_seq OWNED BY basicdb_flowable8.sys_role.role_id;


--
-- TOC entry 425 (class 1259 OID 4489878)
-- Name: sys_user; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_user (
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


ALTER TABLE basicdb_flowable8.sys_user OWNER TO postgres;

--
-- TOC entry 426 (class 1259 OID 4489887)
-- Name: sys_user_post; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_user_post (
    user_id bigint NOT NULL,
    post_id bigint NOT NULL
);


ALTER TABLE basicdb_flowable8.sys_user_post OWNER TO postgres;

--
-- TOC entry 427 (class 1259 OID 4489890)
-- Name: sys_user_role; Type: TABLE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE TABLE basicdb_flowable8.sys_user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE basicdb_flowable8.sys_user_role OWNER TO postgres;

--
-- TOC entry 428 (class 1259 OID 4489893)
-- Name: sys_user_user_id_seq; Type: SEQUENCE; Schema: basicdb_flowable8; Owner: postgres
--

CREATE SEQUENCE basicdb_flowable8.sys_user_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_flowable8.sys_user_user_id_seq OWNER TO postgres;

--
-- TOC entry 4888 (class 0 OID 0)
-- Dependencies: 428
-- Name: sys_user_user_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_flowable8; Owner: postgres
--

ALTER SEQUENCE basicdb_flowable8.sys_user_user_id_seq OWNED BY basicdb_flowable8.sys_user.user_id;


--
-- TOC entry 485 (class 1259 OID 4499720)
-- Name: tbl_flow_extend_procinst; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.tbl_flow_extend_procinst (
    id character varying(32) NOT NULL,
    proc_ins_id character varying(64) DEFAULT NULL::character varying,
    proc_def_id character varying(64) DEFAULT NULL::character varying,
    model_key character varying(64) DEFAULT NULL::character varying,
    model_id character varying(64) DEFAULT NULL::character varying,
    process_name character varying(255) DEFAULT NULL::character varying,
    process_status character varying(20) DEFAULT NULL::character varying,
    current_user_code character varying(64) DEFAULT NULL::character varying,
    tenant_id character varying(64) DEFAULT NULL::character varying,
    form_data text,
    current_name character varying(64) DEFAULT NULL::character varying,
    creator character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE basicdb_flowable8.tbl_flow_extend_procinst OWNER TO system;

--
-- TOC entry 476 (class 1259 OID 4497564)
-- Name: wf_category; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.wf_category (
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


ALTER TABLE basicdb_flowable8.wf_category OWNER TO system;

--
-- TOC entry 477 (class 1259 OID 4497577)
-- Name: wf_category_org_link; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.wf_category_org_link (
    id bigint NOT NULL,
    category_id bigint NOT NULL,
    org_id bigint NOT NULL,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone
);


ALTER TABLE basicdb_flowable8.wf_category_org_link OWNER TO system;

--
-- TOC entry 478 (class 1259 OID 4497582)
-- Name: wf_comment_info; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.wf_comment_info (
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


ALTER TABLE basicdb_flowable8.wf_comment_info OWNER TO system;

--
-- TOC entry 479 (class 1259 OID 4497601)
-- Name: wf_copy; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.wf_copy (
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


ALTER TABLE basicdb_flowable8.wf_copy OWNER TO system;

--
-- TOC entry 480 (class 1259 OID 4497621)
-- Name: wf_deploy_form; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.wf_deploy_form (
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


ALTER TABLE basicdb_flowable8.wf_deploy_form OWNER TO system;

--
-- TOC entry 481 (class 1259 OID 4497633)
-- Name: wf_form; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.wf_form (
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


ALTER TABLE basicdb_flowable8.wf_form OWNER TO system;

--
-- TOC entry 482 (class 1259 OID 4497648)
-- Name: wf_form_category; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.wf_form_category (
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


ALTER TABLE basicdb_flowable8.wf_form_category OWNER TO system;

--
-- TOC entry 483 (class 1259 OID 4497660)
-- Name: wf_form_template; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.wf_form_template (
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


ALTER TABLE basicdb_flowable8.wf_form_template OWNER TO system;

--
-- TOC entry 484 (class 1259 OID 4497671)
-- Name: wf_task_form; Type: TABLE; Schema: basicdb_flowable8; Owner: system
--

CREATE TABLE basicdb_flowable8.wf_task_form (
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


ALTER TABLE basicdb_flowable8.wf_task_form OWNER TO system;

--
-- TOC entry 4076 (class 2604 OID 4490310)
-- Name: act_evt_log log_nr_; Type: DEFAULT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_evt_log ALTER COLUMN log_nr_ SET DEFAULT nextval('basicdb_flowable8.act_evt_log_log_nr__seq'::regclass);


--
-- TOC entry 4067 (class 2604 OID 4490225)
-- Name: act_hi_tsk_log id_; Type: DEFAULT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_hi_tsk_log ALTER COLUMN id_ SET DEFAULT nextval('basicdb_flowable8.act_hi_tsk_log_id__seq'::regclass);


--
-- TOC entry 4003 (class 2604 OID 4489894)
-- Name: gen_table table_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.gen_table ALTER COLUMN table_id SET DEFAULT nextval('basicdb_flowable8.gen_table_table_id_seq'::regclass);


--
-- TOC entry 4007 (class 2604 OID 4489895)
-- Name: gen_table_column column_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.gen_table_column ALTER COLUMN column_id SET DEFAULT nextval('basicdb_flowable8.gen_table_column_column_id_seq'::regclass);


--
-- TOC entry 4009 (class 2604 OID 4489896)
-- Name: sys_config config_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_config ALTER COLUMN config_id SET DEFAULT nextval('basicdb_flowable8.sys_config_config_id_seq'::regclass);


--
-- TOC entry 4011 (class 2604 OID 4489897)
-- Name: sys_dept dept_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_dept ALTER COLUMN dept_id SET DEFAULT nextval('basicdb_flowable8.sys_dept_dept_id_seq'::regclass);


--
-- TOC entry 4016 (class 2604 OID 4489898)
-- Name: sys_dict_data dict_code; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_dict_data ALTER COLUMN dict_code SET DEFAULT nextval('basicdb_flowable8.sys_dict_data_dict_code_seq'::regclass);


--
-- TOC entry 4020 (class 2604 OID 4489899)
-- Name: sys_dict_type dict_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_dict_type ALTER COLUMN dict_id SET DEFAULT nextval('basicdb_flowable8.sys_dict_type_dict_id_seq'::regclass);


--
-- TOC entry 4022 (class 2604 OID 4489900)
-- Name: sys_job job_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_job ALTER COLUMN job_id SET DEFAULT nextval('basicdb_flowable8.sys_job_job_id_seq'::regclass);


--
-- TOC entry 4027 (class 2604 OID 4489901)
-- Name: sys_job_log job_log_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_job_log ALTER COLUMN job_log_id SET DEFAULT nextval('basicdb_flowable8.sys_job_log_job_log_id_seq'::regclass);


--
-- TOC entry 4029 (class 2604 OID 4489902)
-- Name: sys_logininfor info_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_logininfor ALTER COLUMN info_id SET DEFAULT nextval('basicdb_flowable8.sys_logininfor_info_id_seq'::regclass);


--
-- TOC entry 4031 (class 2604 OID 4489903)
-- Name: sys_menu menu_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_menu ALTER COLUMN menu_id SET DEFAULT nextval('basicdb_flowable8.sys_menu_menu_id_seq'::regclass);


--
-- TOC entry 4039 (class 2604 OID 4489904)
-- Name: sys_notice notice_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_notice ALTER COLUMN notice_id SET DEFAULT nextval('basicdb_flowable8.sys_notice_notice_id_seq'::regclass);


--
-- TOC entry 4041 (class 2604 OID 4489905)
-- Name: sys_oper_log oper_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_oper_log ALTER COLUMN oper_id SET DEFAULT nextval('basicdb_flowable8.sys_oper_log_oper_id_seq'::regclass);


--
-- TOC entry 4045 (class 2604 OID 4489906)
-- Name: sys_post post_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_post ALTER COLUMN post_id SET DEFAULT nextval('basicdb_flowable8.sys_post_post_id_seq'::regclass);


--
-- TOC entry 4046 (class 2604 OID 4489907)
-- Name: sys_role role_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_role ALTER COLUMN role_id SET DEFAULT nextval('basicdb_flowable8.sys_role_role_id_seq'::regclass);


--
-- TOC entry 4051 (class 2604 OID 4489908)
-- Name: sys_user user_id; Type: DEFAULT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_user ALTER COLUMN user_id SET DEFAULT nextval('basicdb_flowable8.sys_user_user_id_seq'::regclass);


--
-- TOC entry 4784 (class 0 OID 4490307)
-- Dependencies: 455
-- Data for Name: act_evt_log; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4759 (class 0 OID 4489997)
-- Dependencies: 430
-- Data for Name: act_ge_bytearray; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4758 (class 0 OID 4489992)
-- Dependencies: 429
-- Data for Name: act_ge_property; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--

INSERT INTO basicdb_flowable8.act_ge_property VALUES ('common.schema.version', '8.0.0.0', 1);
INSERT INTO basicdb_flowable8.act_ge_property VALUES ('next.dbid', '1', 1);
INSERT INTO basicdb_flowable8.act_ge_property VALUES ('schema.version', '8.0.0.0', 1);
INSERT INTO basicdb_flowable8.act_ge_property VALUES ('schema.history', 'create(8.0.0.0)', 1);
INSERT INTO basicdb_flowable8.act_ge_property VALUES ('eventregistry.schema.version', '8.0.0.0', 1);
INSERT INTO basicdb_flowable8.act_ge_property VALUES ('cfg.execution-related-entities-count', 'true', 1);
INSERT INTO basicdb_flowable8.act_ge_property VALUES ('cfg.task-related-entities-count', 'true', 1);


--
-- TOC entry 4788 (class 0 OID 4490541)
-- Dependencies: 459
-- Data for Name: act_hi_actinst; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4791 (class 0 OID 4490564)
-- Dependencies: 462
-- Data for Name: act_hi_attachment; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4790 (class 0 OID 4490557)
-- Dependencies: 461
-- Data for Name: act_hi_comment; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4789 (class 0 OID 4490550)
-- Dependencies: 460
-- Data for Name: act_hi_detail; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4761 (class 0 OID 4490015)
-- Dependencies: 432
-- Data for Name: act_hi_entitylink; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4763 (class 0 OID 4490038)
-- Dependencies: 434
-- Data for Name: act_hi_identitylink; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4787 (class 0 OID 4490530)
-- Dependencies: 458
-- Data for Name: act_hi_procinst; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4773 (class 0 OID 4490212)
-- Dependencies: 444
-- Data for Name: act_hi_taskinst; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4775 (class 0 OID 4490222)
-- Dependencies: 446
-- Data for Name: act_hi_tsk_log; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4777 (class 0 OID 4490250)
-- Dependencies: 448
-- Data for Name: act_hi_varinst; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4793 (class 0 OID 4490595)
-- Dependencies: 464
-- Data for Name: act_id_bytearray; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4794 (class 0 OID 4490602)
-- Dependencies: 465
-- Data for Name: act_id_group; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4797 (class 0 OID 4490622)
-- Dependencies: 468
-- Data for Name: act_id_info; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4795 (class 0 OID 4490609)
-- Dependencies: 466
-- Data for Name: act_id_membership; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4799 (class 0 OID 4490636)
-- Dependencies: 470
-- Data for Name: act_id_priv; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4800 (class 0 OID 4490641)
-- Dependencies: 471
-- Data for Name: act_id_priv_mapping; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4792 (class 0 OID 4490590)
-- Dependencies: 463
-- Data for Name: act_id_property; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--

INSERT INTO basicdb_flowable8.act_id_property VALUES ('schema.version', '8.0.0.0', 1);


--
-- TOC entry 4798 (class 0 OID 4490629)
-- Dependencies: 469
-- Data for Name: act_id_token; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4796 (class 0 OID 4490614)
-- Dependencies: 467
-- Data for Name: act_id_user; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4785 (class 0 OID 4490316)
-- Dependencies: 456
-- Data for Name: act_procdef_info; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4779 (class 0 OID 4490273)
-- Dependencies: 450
-- Data for Name: act_re_deployment; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4780 (class 0 OID 4490281)
-- Dependencies: 451
-- Data for Name: act_re_model; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4782 (class 0 OID 4490297)
-- Dependencies: 453
-- Data for Name: act_re_procdef; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4786 (class 0 OID 4490321)
-- Dependencies: 457
-- Data for Name: act_ru_actinst; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4767 (class 0 OID 4490073)
-- Dependencies: 438
-- Data for Name: act_ru_deadletter_job; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4760 (class 0 OID 4490004)
-- Dependencies: 431
-- Data for Name: act_ru_entitylink; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4778 (class 0 OID 4490261)
-- Dependencies: 449
-- Data for Name: act_ru_event_subscr; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4781 (class 0 OID 4490289)
-- Dependencies: 452
-- Data for Name: act_ru_execution; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4769 (class 0 OID 4490089)
-- Dependencies: 440
-- Data for Name: act_ru_external_job; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4768 (class 0 OID 4490081)
-- Dependencies: 439
-- Data for Name: act_ru_history_job; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4762 (class 0 OID 4490026)
-- Dependencies: 433
-- Data for Name: act_ru_identitylink; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4764 (class 0 OID 4490049)
-- Dependencies: 435
-- Data for Name: act_ru_job; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4766 (class 0 OID 4490065)
-- Dependencies: 437
-- Data for Name: act_ru_suspended_job; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4772 (class 0 OID 4490200)
-- Dependencies: 443
-- Data for Name: act_ru_task; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4765 (class 0 OID 4490057)
-- Dependencies: 436
-- Data for Name: act_ru_timer_job; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4776 (class 0 OID 4490235)
-- Dependencies: 447
-- Data for Name: act_ru_variable; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4804 (class 0 OID 4490698)
-- Dependencies: 475
-- Data for Name: flw_channel_definition; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4803 (class 0 OID 4490684)
-- Dependencies: 474
-- Data for Name: flw_event_definition; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4801 (class 0 OID 4490670)
-- Dependencies: 472
-- Data for Name: flw_event_deployment; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4802 (class 0 OID 4490677)
-- Dependencies: 473
-- Data for Name: flw_event_resource; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4815 (class 0 OID 4499741)
-- Dependencies: 486
-- Data for Name: flw_expression; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4770 (class 0 OID 4490178)
-- Dependencies: 441
-- Data for Name: flw_ru_batch; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4771 (class 0 OID 4490186)
-- Dependencies: 442
-- Data for Name: flw_ru_batch_part; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4708 (class 0 OID 4489678)
-- Dependencies: 377
-- Data for Name: gen_table; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--

INSERT INTO basicdb_flowable8.gen_table VALUES (1, 'sys_role_dept', NULL, NULL, NULL, 'SysRoleDept', 'crud', 'com.basic.system', 'system', 'dept', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (2, 'sys_role_menu', NULL, NULL, NULL, 'SysRoleMenu', 'crud', 'com.basic.system', 'system', 'menu', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (3, 'sys_user', NULL, NULL, NULL, 'SysUser', 'crud', 'com.basic.system', 'system', 'user', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (4, 'sys_user_post', NULL, NULL, NULL, 'SysUserPost', 'crud', 'com.basic.system', 'system', 'post', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (5, 'sys_user_role', NULL, NULL, NULL, 'SysUserRole', 'crud', 'com.basic.system', 'system', 'role', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (6, 'sys_notice', NULL, NULL, NULL, 'SysNotice', 'crud', 'com.basic.system', 'system', 'notice', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (7, 'sys_menu', NULL, NULL, NULL, 'SysMenu', 'crud', 'com.basic.system', 'system', 'menu', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (8, 'sys_config', NULL, NULL, NULL, 'SysConfig', 'crud', 'com.basic.system', 'system', 'config', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (9, 'sys_dept', NULL, NULL, NULL, 'SysDept', 'crud', 'com.basic.system', 'system', 'dept', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (10, 'sys_dict_data', NULL, NULL, NULL, 'SysDictData', 'crud', 'com.basic.system', 'system', 'data', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (11, 'sys_dict_type', NULL, NULL, NULL, 'SysDictType', 'crud', 'com.basic.system', 'system', 'type', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (12, 'sys_job', NULL, NULL, NULL, 'SysJob', 'crud', 'com.basic.system', 'system', 'job', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (13, 'sys_job_log', NULL, NULL, NULL, 'SysJobLog', 'crud', 'com.basic.system', 'system', 'log', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (14, 'sys_logininfor', NULL, NULL, NULL, 'SysLogininfor', 'crud', 'com.basic.system', 'system', 'logininfor', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (15, 'sys_oper_log', NULL, NULL, NULL, 'SysOperLog', 'crud', 'com.basic.system', 'system', 'log', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (16, 'sys_post', NULL, NULL, NULL, 'SysPost', 'crud', 'com.basic.system', 'system', 'post', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (17, 'sys_role', NULL, NULL, NULL, 'SysRole', 'crud', 'com.basic.system', 'system', 'role', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table VALUES (18, 'sys_demo', NULL, NULL, NULL, 'SysDemo', 'crud', 'com.basic.system', 'system', 'demo', NULL, 'basic', '0', '/', NULL, 'admin', '2024-02-01 11:09:28', NULL, NULL, NULL);


--
-- TOC entry 4709 (class 0 OID 4489686)
-- Dependencies: 378
-- Data for Name: gen_table_column; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.gen_table_column VALUES (3, '2', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (4, '2', 'menu_id', NULL, 'bigint', 'Long', 'menuId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (5, '3', 'user_id', NULL, 'bigint', 'Long', 'userId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (6, '3', 'dept_id', NULL, 'bigint', 'Long', 'deptId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (7, '3', 'user_name', NULL, 'character varying', 'String', 'userName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 3, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (8, '3', 'nick_name', NULL, 'character varying', 'String', 'nickName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 4, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (9, '3', 'user_type', NULL, 'character varying', 'String', 'userType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 5, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (10, '3', 'email', NULL, 'character varying', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (11, '3', 'phonenumber', NULL, 'character varying', 'String', 'phonenumber', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (12, '3', 'sex', NULL, 'character varying', 'String', 'sex', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 8, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (13, '3', 'avatar', NULL, 'character varying', 'String', 'avatar', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (14, '3', 'password', NULL, 'character varying', 'String', 'password', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (15, '3', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 11, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (16, '3', 'del_flag', NULL, 'character varying', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (17, '3', 'login_ip', NULL, 'character varying', 'String', 'loginIp', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (18, '3', 'login_date', NULL, 'timestamp without time zone', 'String', 'loginDate', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (19, '3', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 15, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (20, '3', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 16, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (21, '3', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 17, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (22, '3', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 18, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (23, '3', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 19, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (24, '4', 'user_id', NULL, 'bigint', 'Long', 'userId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (25, '4', 'post_id', NULL, 'bigint', 'Long', 'postId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (26, '5', 'user_id', NULL, 'bigint', 'Long', 'userId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (27, '5', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (28, '6', 'notice_id', NULL, 'bigint', 'Long', 'noticeId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (29, '6', 'notice_title', NULL, 'character varying', 'String', 'noticeTitle', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (30, '6', 'notice_type', NULL, 'character varying', 'String', 'noticeType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', NULL, 3, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (31, '6', 'notice_content', NULL, 'bytea', 'String', 'noticeContent', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'editor', NULL, 4, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (32, '6', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 5, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (33, '6', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (34, '6', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (35, '6', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (36, '6', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (37, '6', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (38, '7', 'menu_id', NULL, 'bigint', 'Long', 'menuId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (39, '7', 'menu_name', NULL, 'character varying', 'String', 'menuName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (40, '7', 'parent_id', NULL, 'bigint', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 3, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (41, '7', 'order_num', NULL, 'integer', 'Long', 'orderNum', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 4, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (42, '7', 'path', NULL, 'character varying', 'String', 'path', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (43, '7', 'component', NULL, 'character varying', 'String', 'component', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (44, '7', 'query', NULL, 'character varying', 'String', 'query', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (45, '7', 'is_frame', NULL, 'character varying', 'String', 'isFrame', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (46, '7', 'is_cache', NULL, 'character varying', 'String', 'isCache', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (47, '7', 'menu_type', NULL, 'character varying', 'String', 'menuType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 10, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (48, '7', 'visible', NULL, 'character varying', 'String', 'visible', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (49, '7', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 12, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (50, '7', 'perms', NULL, 'character varying', 'String', 'perms', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (51, '7', 'icon', NULL, 'character varying', 'String', 'icon', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (52, '7', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 15, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (53, '7', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 16, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (54, '7', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 17, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (55, '7', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 18, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (56, '7', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 19, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (57, '8', 'config_id', NULL, 'integer', 'Long', 'configId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (58, '8', 'config_name', NULL, 'character varying', 'String', 'configName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (59, '8', 'config_key', NULL, 'character varying', 'String', 'configKey', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (60, '8', 'config_value', NULL, 'character varying', 'String', 'configValue', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (61, '8', 'config_type', NULL, 'character varying', 'String', 'configType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (62, '8', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (63, '8', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (64, '8', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (65, '8', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (66, '8', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (67, '9', 'dept_id', NULL, 'bigint', 'Long', 'deptId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (68, '9', 'parent_id', NULL, 'bigint', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (69, '9', 'ancestors', NULL, 'character varying', 'String', 'ancestors', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (70, '9', 'dept_name', NULL, 'character varying', 'String', 'deptName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (71, '9', 'order_num', NULL, 'integer', 'Long', 'orderNum', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (72, '9', 'leader', NULL, 'character varying', 'String', 'leader', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (73, '9', 'phone', NULL, 'character varying', 'String', 'phone', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (74, '9', 'email', NULL, 'character varying', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (75, '9', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (76, '9', 'del_flag', NULL, 'character varying', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (77, '9', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (78, '9', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (79, '9', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (80, '9', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (81, '10', 'dict_code', NULL, 'bigint', 'Long', 'dictCode', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (82, '10', 'dict_sort', NULL, 'integer', 'Long', 'dictSort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (83, '10', 'dict_label', NULL, 'character varying', 'String', 'dictLabel', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (84, '10', 'dict_value', NULL, 'character varying', 'String', 'dictValue', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (85, '10', 'dict_type', NULL, 'character varying', 'String', 'dictType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (86, '10', 'css_class', NULL, 'character varying', 'String', 'cssClass', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (87, '10', 'list_class', NULL, 'character varying', 'String', 'listClass', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (88, '10', 'is_default', NULL, 'character varying', 'String', 'isDefault', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (89, '10', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (90, '10', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (91, '10', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (92, '10', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (93, '10', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (94, '10', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (95, '11', 'dict_id', NULL, 'bigint', 'Long', 'dictId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (96, '11', 'dict_name', NULL, 'character varying', 'String', 'dictName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (97, '11', 'dict_type', NULL, 'character varying', 'String', 'dictType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (98, '11', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (99, '11', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (100, '11', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (101, '11', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (102, '11', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (103, '11', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (104, '12', 'job_id', NULL, 'bigint', 'Long', 'jobId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (105, '12', 'job_name', NULL, 'character varying', 'String', 'jobName', '1', '0', '0', '1', NULL, NULL, NULL, 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (106, '12', 'job_group', NULL, 'character varying', 'String', 'jobGroup', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (107, '12', 'invoke_target', NULL, 'character varying', 'String', 'invokeTarget', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (108, '12', 'cron_expression', NULL, 'character varying', 'String', 'cronExpression', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (109, '12', 'misfire_policy', NULL, 'character varying', 'String', 'misfirePolicy', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (110, '12', 'concurrent', NULL, 'character varying', 'String', 'concurrent', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (111, '12', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (112, '12', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (113, '12', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (114, '12', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (115, '12', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (116, '12', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (117, '13', 'job_log_id', NULL, 'bigint', 'Long', 'jobLogId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (118, '13', 'job_name', NULL, 'character varying', 'String', 'jobName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (119, '13', 'job_group', NULL, 'character varying', 'String', 'jobGroup', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (120, '13', 'invoke_target', NULL, 'character varying', 'String', 'invokeTarget', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (121, '13', 'job_message', NULL, 'character varying', 'String', 'jobMessage', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (122, '13', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (123, '13', 'exception_info', NULL, 'character varying', 'String', 'exceptionInfo', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (124, '13', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (125, '14', 'info_id', NULL, 'bigint', 'Long', 'infoId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (126, '14', 'user_name', NULL, 'character varying', 'String', 'userName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (127, '14', 'ipaddr', NULL, 'character varying', 'String', 'ipaddr', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (128, '14', 'login_location', NULL, 'character varying', 'String', 'loginLocation', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (129, '14', 'browser', NULL, 'character varying', 'String', 'browser', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (130, '14', 'os', NULL, 'character varying', 'String', 'os', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (131, '14', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (132, '14', 'msg', NULL, 'character varying', 'String', 'msg', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (133, '14', 'login_time', NULL, 'timestamp without time zone', 'String', 'loginTime', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (134, '15', 'oper_id', NULL, 'bigint', 'Long', 'operId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (135, '15', 'title', NULL, 'character varying', 'String', 'title', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (136, '15', 'business_type', NULL, 'integer', 'Long', 'businessType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (137, '15', 'method', NULL, 'character varying', 'String', 'method', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (138, '15', 'request_method', NULL, 'character varying', 'String', 'requestMethod', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (139, '15', 'operator_type', NULL, 'integer', 'Long', 'operatorType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (140, '15', 'oper_name', NULL, 'character varying', 'String', 'operName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (141, '15', 'dept_name', NULL, 'character varying', 'String', 'deptName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (142, '15', 'oper_url', NULL, 'character varying', 'String', 'operUrl', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (143, '15', 'oper_ip', NULL, 'character varying', 'String', 'operIp', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (144, '15', 'oper_location', NULL, 'character varying', 'String', 'operLocation', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (145, '15', 'oper_param', NULL, 'character varying', 'String', 'operParam', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (146, '15', 'json_result', NULL, 'character varying', 'String', 'jsonResult', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (147, '15', 'status', NULL, 'integer', 'Long', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (148, '15', 'error_msg', NULL, 'character varying', 'String', 'errorMsg', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 15, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (149, '15', 'oper_time', NULL, 'timestamp without time zone', 'String', 'operTime', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 16, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (150, '16', 'post_id', NULL, 'bigint', 'Long', 'postId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (151, '16', 'post_code', NULL, 'character varying', 'String', 'postCode', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (152, '16', 'post_name', NULL, 'character varying', 'String', 'postName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (153, '16', 'post_sort', NULL, 'integer', 'Long', 'postSort', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (154, '16', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (155, '16', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (156, '16', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (157, '16', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (158, '16', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (159, '16', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (160, '17', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (161, '17', 'role_name', NULL, 'character varying', 'String', 'roleName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (162, '17', 'role_key', NULL, 'character varying', 'String', 'roleKey', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (163, '17', 'role_sort', NULL, 'integer', 'Long', 'roleSort', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (164, '17', 'data_scope', NULL, 'character varying', 'String', 'dataScope', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (165, '17', 'menu_check_strictly', NULL, 'smallint', 'Long', 'menuCheckStrictly', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (166, '17', 'dept_check_strictly', NULL, 'smallint', 'Long', 'deptCheckStrictly', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (167, '17', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (168, '17', 'del_flag', NULL, 'character varying', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (169, '17', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (170, '17', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (171, '17', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (172, '17', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (173, '17', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (1, '1', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, '2024-01-16 11:33:08');
INSERT INTO basicdb_flowable8.gen_table_column VALUES (2, '1', 'dept_id', NULL, 'bigint', 'Long', 'deptId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, '2024-01-16 11:33:08');
INSERT INTO basicdb_flowable8.gen_table_column VALUES (174, '18', 'id', NULL, 'bigint', 'Long', 'id', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (175, '18', 'name', NULL, 'character varying', 'String', 'name', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (176, '18', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 3, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (177, '18', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 4, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (178, '18', 'create_id', NULL, 'bigint', 'Long', 'createId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 5, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (179, '18', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (180, '18', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (181, '18', 'update_id', NULL, 'bigint', 'Long', 'updateId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 8, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (182, '18', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 9, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (183, '18', 'is_del', NULL, 'character varying', 'String', 'isDel', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 10, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (184, '18', 'sex', NULL, 'character varying', 'String', 'sex', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 11, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_flowable8.gen_table_column VALUES (185, '18', 'birth', NULL, 'timestamp without time zone', 'String', 'birth', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 12, 'admin', '2024-02-01 11:09:28', NULL, NULL);


--
-- TOC entry 4712 (class 0 OID 4489704)
-- Dependencies: 383
-- Data for Name: qrtz_blob_triggers; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4713 (class 0 OID 4489709)
-- Dependencies: 384
-- Data for Name: qrtz_calendars; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4714 (class 0 OID 4489714)
-- Dependencies: 385
-- Data for Name: qrtz_cron_triggers; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4715 (class 0 OID 4489719)
-- Dependencies: 386
-- Data for Name: qrtz_fired_triggers; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4716 (class 0 OID 4489724)
-- Dependencies: 387
-- Data for Name: qrtz_job_details; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4717 (class 0 OID 4489729)
-- Dependencies: 388
-- Data for Name: qrtz_locks; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4718 (class 0 OID 4489732)
-- Dependencies: 389
-- Data for Name: qrtz_paused_trigger_grps; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4719 (class 0 OID 4489735)
-- Dependencies: 390
-- Data for Name: qrtz_scheduler_state; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4720 (class 0 OID 4489738)
-- Dependencies: 391
-- Data for Name: qrtz_simple_triggers; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4721 (class 0 OID 4489743)
-- Dependencies: 392
-- Data for Name: qrtz_simprop_triggers; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4722 (class 0 OID 4489748)
-- Dependencies: 393
-- Data for Name: qrtz_triggers; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4723 (class 0 OID 4489753)
-- Dependencies: 394
-- Data for Name: sys_config; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_config VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO basicdb_flowable8.sys_config VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO basicdb_flowable8.sys_config VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO basicdb_flowable8.sys_config VALUES (10, '账号自助-密码有效期天数', 'sys.account.checkPassword.time', '90', 'Y', 'admin', '2024-02-21 11:21:18', 'admin', '2024-10-11 10:14:33', '对90天未修改密码的账号进行提醒');
INSERT INTO basicdb_flowable8.sys_config VALUES (7, '密码复杂度验证表达式', 'sys.password.inputPattern', '/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[`!@#$%^&*(\)-//.,;'':"{}\[\]\\|]).{8,15}$/', 'Y', 'admin', '2024-02-20 15:00:42', 'admin', '2024-02-20 15:55:26', '/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[`!@#$%^&*(\)-//.,;'':"{}\[\]\\|]).{8,15}$/');
INSERT INTO basicdb_flowable8.sys_config VALUES (8, '密码复杂度验证提示信息', 'sys.password.errorMessage', '用户密码长度必须介于 8 和 15 之间且包含大小写字母、数字及特殊字符', 'Y', 'admin', '2024-02-20 15:11:04', 'admin', '2024-02-20 16:03:43', NULL);
INSERT INTO basicdb_flowable8.sys_config VALUES (9, '账号自助-密码修改提醒', 'sys.account.checkPassword', 'true', 'Y', 'admin', '2024-02-21 10:59:19', NULL, NULL, '是否开启修改密码提醒(对长时间没有修改密码的账号)（true开启，false关闭）');
INSERT INTO basicdb_flowable8.sys_config VALUES (2, '账号管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '初始化密码 123456');
INSERT INTO basicdb_flowable8.sys_config VALUES (11, '账号登录-是否开启账号锁定', 'user.password.lockOpen', 'true', 'Y', 'admin', '2024-02-21 11:23:26', NULL, NULL, '是否开启密码错误次数过多账号锁定（true开启，false关闭）');
INSERT INTO basicdb_flowable8.sys_config VALUES (12, '账号登录-密码错误次数锁定', 'user.password.maxRetryCount ', '3', 'Y', 'admin', '2024-02-21 11:25:02', NULL, NULL, '账号登录时密码最大错误次数');
INSERT INTO basicdb_flowable8.sys_config VALUES (13, '账号登录-账号锁定时间', 'user.password.lockTime', '30', 'Y', 'admin', '2024-02-21 11:26:27', NULL, NULL, '账号锁定时长（分钟）');
INSERT INTO basicdb_flowable8.sys_config VALUES (4, '账号自助-验证码开关', 'sys.account.captchaOnOff', 'true', 'Y', 'admin', '2023-02-28 17:09:30', 'admin', '2026-10-17 19:20:21', '是否开启验证码功能（true开启，false关闭）');


--
-- TOC entry 4725 (class 0 OID 4489760)
-- Dependencies: 396
-- Data for Name: sys_demo; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--

INSERT INTO basicdb_flowable8.sys_demo VALUES (1750711753414205442, '李四', 'admin', '2024-01-26 10:46:23', 1, 'admin', '2024-02-19 11:34:34.706', NULL, NULL, '0', 'boy', '2024-01-09 00:00:00');


--
-- TOC entry 4726 (class 0 OID 4489763)
-- Dependencies: 397
-- Data for Name: sys_dept; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_dept VALUES (103, 101, '0,100,101', '研发部门', 1, '部门管理员', '15888888888', 'bdvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-07-10 17:08:12', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_dept VALUES (101, 100, '0,100', '佛山总公司', 1, '佛山管理员', '18888888888', 'bdvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-01-26 14:39:26', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_dept VALUES (100, 0, '0', '集团公司', 0, '管理员', '18888888888', 'dbvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-02-08 08:49:44', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_dept VALUES (102, 100, '0,100', '长沙分公司', 2, '长沙管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_dept VALUES (104, 101, '0,100,101', '市场部门', 2, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_dept VALUES (105, 101, '0,100,101', '测试部门', 3, '部门管理员', '15888888888', 'bdvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-02-08 08:49:31', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_dept VALUES (106, 101, '0,100,101', '财务部门', 4, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_dept VALUES (107, 101, '0,100,101', '运维部门', 5, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_dept VALUES (108, 102, '0,100,102', '市场部门', 1, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_dept VALUES (109, 102, '0,100,102', '财务部门', 2, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_dept VALUES (111, 101, '0,100,101', '办公室', 3, NULL, NULL, NULL, '0', '0', 'xiaoxi', '2024-04-22 16:04:38', NULL, NULL, NULL, NULL, NULL);


--
-- TOC entry 4728 (class 0 OID 4489773)
-- Dependencies: 399
-- Data for Name: sys_dict_data; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_dict_data VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '性别男');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '性别女');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '性别未知');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '显示菜单');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '隐藏菜单');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '停用状态');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '停用状态');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '默认分组');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统分组');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统默认是');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统默认否');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '通知');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '公告');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '关闭状态');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (18, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '新增操作');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (19, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '修改操作');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (20, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '删除操作');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (21, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '授权操作');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (22, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '导出操作');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (23, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '导入操作');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (24, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '强退操作');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (25, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '生成操作');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (26, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '清空操作');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (27, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (28, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '停用状态');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (30, 0, '跟随系统', '0', 'sys_show_tags', NULL, 'default', 'N', '0', 'admin', '2024-06-25 11:15:44', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (31, 1, '显示', '1', 'sys_show_tags', NULL, 'default', 'N', '0', 'admin', '2024-06-25 11:16:01', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (32, 2, '隐藏', '2', 'sys_show_tags', NULL, 'default', 'N', '0', 'admin', '2024-06-25 11:17:07', 'admin', '2024-06-25 11:17:13', NULL);
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (46, 1, '自定义表单', 'custom', 'wf_process_form_type', NULL, 'primary', 'N', '0', 'admin', '2026-10-17 18:57:55', NULL, NULL, 'VFormDesigner 自定义');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (47, 2, '系统表单', 'system', 'wf_process_form_type', NULL, 'info', 'N', '0', 'admin', '2026-10-17 18:57:55', NULL, NULL, '系统内置表单');
INSERT INTO basicdb_flowable8.sys_dict_data VALUES (48, 3, '外部表单', 'external', 'wf_process_form_type', NULL, 'success', 'N', '0', 'admin', '2026-10-17 18:57:55', NULL, NULL, '外部业务表单');


--
-- TOC entry 4730 (class 0 OID 4489782)
-- Dependencies: 401
-- Data for Name: sys_dict_type; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_dict_type VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '用户性别列表');
INSERT INTO basicdb_flowable8.sys_dict_type VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '菜单状态列表');
INSERT INTO basicdb_flowable8.sys_dict_type VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统开关列表');
INSERT INTO basicdb_flowable8.sys_dict_type VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '任务状态列表');
INSERT INTO basicdb_flowable8.sys_dict_type VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '任务分组列表');
INSERT INTO basicdb_flowable8.sys_dict_type VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统是否列表');
INSERT INTO basicdb_flowable8.sys_dict_type VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '通知类型列表');
INSERT INTO basicdb_flowable8.sys_dict_type VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '通知状态列表');
INSERT INTO basicdb_flowable8.sys_dict_type VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '操作类型列表');
INSERT INTO basicdb_flowable8.sys_dict_type VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '登录状态列表');
INSERT INTO basicdb_flowable8.sys_dict_type VALUES (12, '选项卡状态', 'sys_show_tags', '0', 'admin', '2024-06-25 11:14:52', 'admin', '2024-06-25 11:14:59', '自定义展示系统Tags-View组件');
INSERT INTO basicdb_flowable8.sys_dict_type VALUES (15, '流程表单类型', 'wf_process_form_type', '0', 'admin', '2026-10-17 18:57:55', NULL, NULL, '表单类型');


--
-- TOC entry 4732 (class 0 OID 4489789)
-- Dependencies: 403
-- Data for Name: sys_files; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--

INSERT INTO basicdb_flowable8.sys_files VALUES (2111420977300377602, 't.png', 'D:/basic/uploadPath/upload/2026/10/17/t_20261017193603A001.png', '/upload/2026/10/17/t_20261017193603A001.png', 8, NULL, NULL, '2026-10-17 19:36:04', 'admin', '0', NULL, NULL, NULL, NULL);


--
-- TOC entry 4733 (class 0 OID 4489794)
-- Dependencies: 404
-- Data for Name: sys_i18n; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--

INSERT INTO basicdb_flowable8.sys_i18n VALUES ('Assembly.buttonPosition', 'Please configure the buttonPosition property for the chart', 'en');
INSERT INTO basicdb_flowable8.sys_i18n VALUES ('Assembly.buttonPosition', '请给图表配置buttonPosition属性', 'zh_CN');
INSERT INTO basicdb_flowable8.sys_i18n VALUES ('Assembly.buttonPosition', '請給圖表配置buttonPosition屬性', 'zh_hant');
INSERT INTO basicdb_flowable8.sys_i18n VALUES ('Assembly.idFormStyle', 'Please configure the idFormStyle property for the chart for more information', 'en');
INSERT INTO basicdb_flowable8.sys_i18n VALUES ('Assembly.idFormStyle', '请给图表配置idFormStyle属性', 'zh_CN');
INSERT INTO basicdb_flowable8.sys_i18n VALUES ('Assembly.idFormStyle', '請給圖表配置idFormStyle屬性', 'zh_hant');


--
-- TOC entry 4734 (class 0 OID 4489799)
-- Dependencies: 405
-- Data for Name: sys_job; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_job VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2023-02-28 17:09:30', '', NULL, '');
INSERT INTO basicdb_flowable8.sys_job VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(''ry'')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2023-02-28 17:09:30', '', NULL, '');
INSERT INTO basicdb_flowable8.sys_job VALUES (3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(''ry'', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2023-02-28 17:09:30', '', NULL, '');


--
-- TOC entry 4736 (class 0 OID 4489809)
-- Dependencies: 407
-- Data for Name: sys_job_log; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4738 (class 0 OID 4489816)
-- Dependencies: 409
-- Data for Name: sys_logininfor; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_logininfor VALUES (725, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '1', '验证码已失效', '2026-10-16 23:02:31');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (726, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '1', '验证码错误', '2026-10-16 23:03:01');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (727, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '1', '验证码已失效', '2026-10-17 13:06:27');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (728, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '1', 'Padding error in decryption', '2026-10-17 13:07:30');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (729, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '0', '登录成功', '2026-10-17 13:08:11');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (730, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '0', '登录成功', '2026-10-17 13:08:16');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (731, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '0', '登录成功', '2026-10-17 13:09:06');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (732, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '0', '登录成功', '2026-10-17 18:11:57');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (733, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '0', '登录成功', '2026-10-17 18:12:41');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (734, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '0', '登录成功', '2026-10-17 18:15:00');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (735, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '0', '登录成功', '2026-10-17 18:15:12');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (736, 'admin', '127.0.0.1', '内网IP', 'WindowsPowerShell', 'Windows NT', '1', 'Padding error in decryption', '2026-10-17 18:44:45');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (737, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 18:45:30');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (738, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 18:46:00');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (739, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 18:48:11');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (740, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 18:49:50');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (741, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 18:50:06');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (742, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 18:50:25');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (743, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 18:57:13');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (744, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 19:04:42');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (745, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 19:06:06');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (746, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 19:06:12');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (747, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 19:06:18');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (748, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 19:06:26');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (749, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 19:06:36');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (750, 'admin', '127.0.0.1', '内网IP', 'Chrome', 'Windows NT', '0', '登录成功', '2026-10-17 19:12:31');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (751, 'admin', '127.0.0.1', '内网IP', 'Chrome', 'Windows NT', '0', '退出成功', '2026-10-17 19:20:26');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (752, 'admin', '127.0.0.1', '内网IP', 'Chrome', 'Windows NT', '0', '登录成功', '2026-10-17 19:20:31');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (753, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '1', '验证码已失效', '2026-10-17 19:24:15');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (754, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '1', '验证码已失效', '2026-10-17 19:24:20');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (755, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '1', '验证码已失效', '2026-10-17 19:24:30');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (756, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '1', '验证码错误', '2026-10-17 19:25:02');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (757, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '1', '验证码错误', '2026-10-17 19:25:23');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (758, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 19:26:38');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (759, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 19:26:49');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (760, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 19:36:04');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (761, 'admin', '127.0.0.1', '内网IP', 'Java-http-client', 'Cloud', '0', '登录成功', '2026-10-17 19:36:31');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (762, 'admin', '127.0.0.1', '内网IP', 'Chrome', 'Windows NT', '0', '登录成功', '2026-10-17 20:32:08');
INSERT INTO basicdb_flowable8.sys_logininfor VALUES (763, 'admin', '127.0.0.1', '内网IP', 'Chrome', 'Windows NT', '0', '登录成功', '2026-10-17 21:07:11');


--
-- TOC entry 4740 (class 0 OID 4489823)
-- Dependencies: 411
-- Data for Name: sys_menu; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_menu VALUES (3, '系统工具', 0, 3, 'tool', NULL, '', '1', '0', 'M', '0', '0', '', 'tool', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-13 16:16:59', '系统工具目录', NULL, '0', 'system tools', '系統工具');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1, '系统管理', 0, 1, 'system', NULL, '', '1', '0', 'M', '0', '0', '', 'system', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-13 16:17:15', '系统管理目录', NULL, '0', 'system management', '系統管理');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1013, '菜单查询', 102, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1014, '菜单新增', 102, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1015, '菜单修改', 102, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1016, '菜单删除', 102, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1024, '岗位删除', 104, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:post:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1025, '岗位导出', 104, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:post:export', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (108, '日志管理', 1, 9, 'log', '', '', '1', '0', 'M', '0', '0', '', 'log', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:02', '日志管理菜单', NULL, '0', 'log management', '日誌管理');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1045, '日志导出', 501, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1001, '用户查询', 100, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:user:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', 'user query', '用戶查詢');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1002, '用户新增', 100, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:user:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1003, '用户修改', 100, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:user:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1008, '角色查询', 101, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:role:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1009, '角色新增', 101, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:role:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1010, '角色修改', 101, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:role:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1011, '角色删除', 101, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:role:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:55', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1019, '部门修改', 103, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1020, '部门删除', 103, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1030, '字典导出', 105, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1058, '导入代码', 115, 2, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1057, '生成删除', 115, 3, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1059, '预览代码', 115, 4, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1060, '生成代码', 115, 5, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1047, '批量强退', 109, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:05', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1004, '用户删除', 100, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:user:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1005, '用户导出', 100, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:user:export', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1006, '用户导入', 100, 6, '', '', '', '1', '0', 'F', '0', '0', 'system:user:import', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1007, '重置密码', 100, 7, '', '', '', '1', '0', 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1012, '角色导出', 101, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:role:export', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:55', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1017, '部门查询', 103, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:14', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1018, '部门新增', 103, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:14', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1021, '岗位查询', 104, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:post:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1022, '岗位新增', 104, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:post:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1023, '岗位修改', 104, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:post:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1026, '字典查询', 105, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1027, '字典新增', 105, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1028, '字典修改', 105, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1029, '字典删除', 105, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1031, '参数查询', 106, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1032, '参数新增', 106, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:add', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1033, '参数修改', 106, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (100, '用户管理', 1, 1, 'user', 'basic-admin/system/user/index', '', '1', '0', 'C', '0', '0', 'system:user:list', 'user', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '用户管理菜单', NULL, '0', 'user management', '用戶管理');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1034, '参数删除', 106, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1036, '公告查询', 107, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1037, '公告新增', 107, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:add', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1038, '公告修改', 107, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1039, '公告删除', 107, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1040, '操作查询', 500, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:09', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1041, '操作删除', 500, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:09', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1042, '日志导出', 500, 4, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:09', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1043, '登录查询', 501, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1044, '登录删除', 501, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1055, '生成查询', 115, 1, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1056, '生成修改', 115, 2, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1046, '在线查询', 109, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:05', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1049, '任务查询', 110, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:12', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1050, '任务新增', 110, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:12', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1035, '参数导出', 106, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1048, '单条强退', 109, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:05', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1051, '任务修改', 110, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1052, '任务删除', 110, 4, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1053, '状态修改', 110, 5, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (1054, '任务导出', 110, 7, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_menu VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', '1', '0', 'M', '1', '1', '', 'monitor', 'admin', '2023-02-28 17:09:30', 'admin', '2026-10-16 17:07:09', '系统监控目录', NULL, '0', 'system monitoring', '系統監控');
INSERT INTO basicdb_flowable8.sys_menu VALUES (101, '角色管理', 1, 2, 'role', 'basic-admin/system/role/index', '', '1', '0', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '角色管理菜单', NULL, '0', 'role management', '角色管理');
INSERT INTO basicdb_flowable8.sys_menu VALUES (102, '菜单管理', 1, 3, 'menu', 'basic-admin/system/menu/index', '', '1', '0', 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:36', '菜单管理菜单', NULL, '0', 'menu management', '菜單管理');
INSERT INTO basicdb_flowable8.sys_menu VALUES (103, '部门管理', 1, 4, 'dept', 'basic-admin/system/dept/index', '', '1', '0', 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:14', '部门管理菜单', NULL, '0', 'sector management', '部門管理');
INSERT INTO basicdb_flowable8.sys_menu VALUES (104, '岗位管理', 1, 5, 'post', 'basic-admin/system/post/index', '', '1', '0', 'C', '0', '0', 'system:post:list', 'post', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:35', '岗位管理菜单', NULL, '0', 'post management', '崗位管理');
INSERT INTO basicdb_flowable8.sys_menu VALUES (105, '字典管理', 1, 6, 'dict', 'basic-admin/system/dict/index', '', '1', '0', 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:42', '字典管理菜单', NULL, '0', 'dictionary management', '字典管理');
INSERT INTO basicdb_flowable8.sys_menu VALUES (107, '通知公告', 1, 8, 'notice', 'basic-admin/system/notice/index', '', '1', '0', 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:57', '通知公告菜单', NULL, '0', 'notice announcement', '通知公告');
INSERT INTO basicdb_flowable8.sys_menu VALUES (106, '参数设置', 1, 7, 'config', 'basic-admin/system/config/index', '', '1', '0', 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:51', '参数设置菜单', NULL, '0', 'parameter setting', '參數設置');
INSERT INTO basicdb_flowable8.sys_menu VALUES (500, '操作日志', 108, 1, 'operlog', 'basic-admin/monitor/operlog/index', '', '1', '0', 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:09', '操作日志菜单', NULL, '0', 'operation log', '操作日誌');
INSERT INTO basicdb_flowable8.sys_menu VALUES (501, '登录日志', 108, 2, 'logininfor', 'basic-admin/monitor/logininfor/index', '', '1', '0', 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:15', '登录日志菜单', NULL, '0', 'login logs', '登錄日誌');
INSERT INTO basicdb_flowable8.sys_menu VALUES (111, '数据监控', 3, 4, 'druid', 'basic-admin/monitor/druid/index', '', '1', '0', 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:59', '数据监控菜单', NULL, '0', 'data monitoring', '數據監控');
INSERT INTO basicdb_flowable8.sys_menu VALUES (109, '在线用户', 3, 5, 'online', 'basic-admin/monitor/online/index', '', '1', '0', 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:05', '在线用户菜单', NULL, '0', 'online users', '在線用戶');
INSERT INTO basicdb_flowable8.sys_menu VALUES (112, '服务监控', 3, 7, 'server', 'basic-admin/monitor/server/index', '', '1', '0', 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:21', '服务监控菜单', NULL, '0', 'service monitoring', '服務監控');
INSERT INTO basicdb_flowable8.sys_menu VALUES (113, '缓存监控', 3, 8, 'cache', 'basic-admin/monitor/cache/index', '', '1', '0', 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:27', '缓存监控菜单', NULL, '0', 'Cache monitoring', '緩存監控');
INSERT INTO basicdb_flowable8.sys_menu VALUES (110, '定时任务', 3, 6, 'job', 'basic-admin/monitor/job/index', '', '1', '0', 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:11', '定时任务菜单', NULL, '0', 'scheduled task', '定時任務');
INSERT INTO basicdb_flowable8.sys_menu VALUES (114, '表单构建', 3, 1, 'build', 'basic-admin/tool/build/index', '', '1', '0', 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:36', '表单构建菜单', NULL, '0', 'Form construction', '表單構建');
INSERT INTO basicdb_flowable8.sys_menu VALUES (115, '代码生成', 3, 2, 'gen', 'basic-admin/tool/gen/index', '', '1', '0', 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:44', '代码生成菜单', NULL, '0', 'code generation', '代碼生成');
INSERT INTO basicdb_flowable8.sys_menu VALUES (116, '系统接口', 3, 3, 'swagger', 'basic-admin/tool/swagger/index', '', '1', '0', 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:54', '系统接口菜单', NULL, '0', 'system interface', '系統接口');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1561, '表单新增', 1509, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:saveForm', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1530, '分类查询', 1506, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:query', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1531, '分类新增', 1506, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:add', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1532, '分类修改', 1506, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:edit', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1533, '分类删除', 1506, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:remove', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1540, '模型查询', 1507, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:list', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1541, '模型新增', 1507, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:add', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1542, '模型修改', 1507, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:edit', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1543, '模型删除', 1507, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:remove', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1544, '模型部署', 1507, 5, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:deploy', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1560, '表单查询', 1509, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:getInfo', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1505, '待签收', 1500, 5, 'task/claim', 'basic-extend/workflow/task/claim', NULL, '1', '0', 'C', '0', '0', 'workflow:process:claimList', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '待签收任务', NULL, NULL, 'Claim', '待簽收');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1500, '流程引擎', 0, 5, 'workflow', NULL, NULL, '1', '0', 'M', '0', '0', '', 'tree', 'admin', '2026-10-14 16:31:42', 'admin', '2026-10-15 08:40:09', '工作流目录', NULL, NULL, 'Workflow', '工作流程');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1506, '流程分类', 1500, 6, 'category', 'basic-extend/workflow/category/index', NULL, '1', '0', 'C', '0', '0', 'workflow:category:list', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '流程分类管理', NULL, NULL, 'Category', '流程分類');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1510, '表达式管理', 1500, 10, 'expression', 'basic-extend/workflow/expression/index', NULL, '1', '0', 'C', '0', '0', 'workflow:expression:list', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '表达式字典管理', NULL, NULL, 'Expression', '表達式管理');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1509, '表单管理', 1500, 9, 'form', 'basic-extend/workflow/form/index', NULL, '1', '0', 'C', '0', '0', 'workflow:form:list', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '流程表单管理', NULL, NULL, 'Form', '表單管理');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1507, '流程模型', 1500, 7, 'model', 'basic-extend/workflow/model/index', NULL, '1', '0', 'C', '0', '0', 'workflow:model:list', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '流程模型管理', NULL, NULL, 'Model', '流程模型');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1501, '我的待办', 1500, 1, 'task/todo', 'basic-extend/workflow/task/todo', NULL, '1', '0', 'C', '0', '0', 'workflow:process:todoList', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '我的待办任务', NULL, NULL, 'Todo', '我的待辦');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1502, '我的已办', 1500, 2, 'task/finished', 'basic-extend/workflow/task/finished', NULL, '1', '0', 'C', '0', '0', 'workflow:process:finishedList', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '我的已办任务', NULL, NULL, 'Finished', '我的已辦');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1503, '我的已发', 1500, 3, 'task/own', 'basic-extend/workflow/task/own', NULL, '1', '0', 'C', '0', '0', 'workflow:process:ownList', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '我发起的流程', NULL, NULL, 'Own', '我的已發');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1504, '抄送我的', 1500, 4, 'task/copy', 'basic-extend/workflow/task/copy', NULL, '1', '0', 'C', '0', '0', 'workflow:process:copyList', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '抄送我的任务', NULL, NULL, 'Copy', '抄送我的');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1580, '表达式查询', 1510, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:list', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1581, '表达式新增', 1510, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:add', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1582, '表达式修改', 1510, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:edit', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1583, '表达式删除', 1510, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:remove', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1570, '任务审批', 1501, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:complete', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1571, '任务拒绝', 1501, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:reject', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1572, '任务转办', 1501, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:transfer', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1573, '任务委派', 1501, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:delegate', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1574, '任务退回', 1501, 5, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:return', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1575, '任务加签', 1501, 6, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:beforeAddSignTask', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1576, '任务终止', 1501, 7, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:cancel', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1577, '流程撤回', 1503, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:revokeProcess', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1562, '表单修改', 1509, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:saveForm', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1563, '表单删除', 1509, 4, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:delForm', '#', 'admin', '2026-10-14 16:31:42', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1601, '分类导出', 1506, 1, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:export', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1602, '分类下拉选项', 1506, 2, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:getCategoryOption', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1603, '分类列表(不分页)', 1506, 3, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:category:listWithoutPage', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1606, '表达式导出', 1510, 6, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:export', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1607, '表达式详情', 1510, 7, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:expression:getInfo', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1608, '按编码查表单', 1509, 8, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:getFormByCode', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1609, '表单控件列表', 1509, 9, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:form:getVFormWidgetList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1610, '表单模板全量', 1509, 10, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:allList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1611, '表单模板删除', 1509, 11, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:delFormTemplate', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1612, '表单模板详情', 1509, 12, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:getInfo', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1613, '表单模板查询', 1509, 13, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:list', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1614, '表单模板保存', 1509, 14, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:formTemplate:saveForm', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1615, '模型激活', 1507, 15, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:activate', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1616, '模型XML', 1507, 16, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:bpmnXml', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1617, '模型导出', 1507, 17, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:export', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1618, '分类下模型', 1507, 18, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:getCategoryToModel', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1619, '模型详情', 1507, 19, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:getInfo', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1620, '历史模型', 1507, 20, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:historyList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1621, '设为最新', 1507, 21, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:latest', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1622, '可发起模型', 1507, 22, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:listModelLaunch', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1623, '模型发布(发起)', 1507, 23, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:publishModel', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1624, '模型保存', 1507, 24, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:save', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1625, '模型挂起', 1507, 25, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:model:suspended', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1626, '流程作废', 1500, 26, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:abolishProcess', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1627, '后加签', 1500, 27, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:afterAddSignTask', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1628, '流程XML', 1500, 28, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:bpmnXml', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1629, '检查流程是否结束', 1500, 29, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:checkFlowIsEnd', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1630, '是否审批人', 1500, 30, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:checkIsAuditUser', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1631, '是否发起人', 1500, 31, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:checkIsStartUser', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1632, '流程是否存在', 1500, 32, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:checkProcessIsExistByProcInsId', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1633, '签收任务', 1500, 33, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:claim', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1634, '待签收导出', 1500, 34, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:claimExport', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1635, '按业务key完成', 1500, 35, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:completeByBusinessKey', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1636, '抄送导出', 1500, 36, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:copyExport', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1637, '删除流程实例', 1500, 37, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:deleteProcessByIds', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1638, '删除流程任务', 1500, 38, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:deleteProcessTask', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1639, '流程详情', 1500, 39, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:detail', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1640, '流程图', 1500, 40, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:diagram', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1641, '变更处理人', 1500, 41, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:editProcessTaskAssignee', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1642, '已办导出', 1500, 42, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:finishedExport', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1643, '抄送流程列表', 1500, 43, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getCopyProcessList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1644, '当前任务信息', 1500, 44, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getCurrentTaskInfo', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1645, '当前任务节点', 1500, 45, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getCurrentTaskNode', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1646, '流程附件', 1500, 46, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getFilesByProcessInstanceId', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1647, '流程流转图', 1500, 47, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getFlowViewer', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1648, '下一节点', 1500, 48, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getNextNode', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1649, '我发起的(不分页)', 1500, 49, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getOwnProcessList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1650, '实例扩展信息', 1500, 50, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcInsIdToExtends', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1651, '实例任务', 1500, 51, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcInsIdToTask', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1652, '审批意见', 1500, 52, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcessCommentInfo', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1653, '流程表单', 1500, 53, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcessForm', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1654, '按业务key查流程', 1500, 54, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcessInfoByBusinessKey', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1655, '流程模型key', 1500, 55, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcessModelKey', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1656, '流程变量', 1500, 56, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getProcessVariables', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1657, '待办与抄送', 1500, 57, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getTaskTodoAndCopy', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1658, '任务变量', 1500, 58, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getTaskVariables', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1659, '待发起模型', 1500, 59, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:getTodoModel', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1660, '历史节点', 1500, 60, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:historyProcNodeList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1661, '会签回退', 1500, 61, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:mulitiSequentialUnComplete', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1662, '下一节点类型', 1500, 62, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:nextFlowNode', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1663, '我的已发导出', 1500, 63, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:ownExport', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1664, '流程表单列表', 1500, 64, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:processFormList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1665, '抄送已读', 1500, 65, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:readCopy', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1666, '驳回到上一节点', 1500, 66, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:rejectLastTask', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1667, '可回退节点', 1500, 67, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:returnList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1668, '保存附件到模型', 1500, 68, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:saveFileToModel', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1669, '已办(选择)', 1500, 69, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:selectFinishedProcessList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1670, '历史实例', 1500, 70, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:selectHisProcInsProcess', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1671, '所有任务', 1500, 71, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:selectProcessAllTask', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1672, '所有实例', 1500, 72, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:selectProcessPrcInstance', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1673, '用户任务', 1500, 73, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:selectProcessTaskByUser', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1674, '跳过节点', 1500, 74, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:skipUserTask', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1675, '待办导出', 1500, 75, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:todoExport', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1676, '取消签收', 1500, 76, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:unClaim', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1677, '更新任务评论', 1500, 77, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:updateTaskComment', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1678, '表单分类新增', 1509, 78, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:systemCategory:add', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1679, '表单分类修改', 1509, 79, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:systemCategory:edit', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1680, '表单分类查询', 1509, 80, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:systemCategory:list', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1681, '表单分类删除', 1509, 81, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:systemCategory:remove', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1682, '删除流程表单', 1500, 82, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:delProcessForm', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1683, '按业务key删表单', 1500, 83, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:delProcessFormByBusinessKey', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1684, '按实例与节点删表单', 1500, 84, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:delProcessFormByProcInsIdAndActId', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1685, '保存流程表单', 1500, 85, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:saveProcessForm', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1686, '按实例查表单数据', 1500, 86, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:selectFormDataByProcInsIdToList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1687, '按业务key查表单列表', 1500, 87, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:selectFormJsonDataByBusinessKeyToList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1688, '按实例与节点查表单', 1500, 88, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:selectFormJsonDataByProcInsIdAndActIdToList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1689, '按实例与业务key查表单', 1500, 89, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:selectFormJsonDataByProcInsIdAndBusinessKey', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1690, '按任务与节点查表单', 1500, 90, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:selectFormJsonDataByTaskIdAndActIdToList', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1691, '按业务key改字段', 1500, 91, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:taskForm:updateFieldValueByBusinessKey', '#', 'admin', '2026-10-14 22:53:13', NULL, NULL, '', NULL, NULL, '', '');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1699, '测试启动流程', 1500, 20, 'process/start', 'basic-extend/workflow/process/start', NULL, '1', '0', 'C', '0', '0', 'workflow:model:publishModel', '#', 'admin', '2026-10-15 10:15:13', NULL, NULL, '快速测试流程发起与流转（仅测试用途）', NULL, NULL, 'Test Start', '測試啟動流程');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1511, '流程中心', 1500, 0, 'center', 'basic-extend/workflow/center/index', NULL, '1', '0', 'C', '0', '0', 'workflow:process:selectProcessPrcInstance', '#', 'admin', '2026-10-15 11:33:56', NULL, NULL, '系统上正在流转的流程，可直接废除', NULL, NULL, 'Process Center', '流程中心');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1692, '清空流程数据', 1500, 92, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:clearAll', '#', 'admin', '2026-10-17 19:03:58', NULL, NULL, '流程清盘', NULL, NULL, 'ClearProcessData', '清空流程數據');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1693, '流程迁移', 1500, 93, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'workflow:process:migrate', '#', 'admin', '2026-10-17 19:03:58', NULL, NULL, '流程版本迁移', NULL, NULL, 'ProcessMigrate', '流程遷移');
INSERT INTO basicdb_flowable8.sys_menu VALUES (1694, '文件上传', 1, 99, NULL, NULL, NULL, '1', '0', 'F', '0', '0', 'common:file:upload', '#', 'admin', '2026-10-17 19:34:45', NULL, NULL, '通用上传接口权限', NULL, NULL, 'FileUpload', '文件上傳');


--
-- TOC entry 4742 (class 0 OID 4489836)
-- Dependencies: 413
-- Data for Name: sys_notice; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--



--
-- TOC entry 4744 (class 0 OID 4489843)
-- Dependencies: 415
-- Data for Name: sys_notice_read; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4746 (class 0 OID 4489847)
-- Dependencies: 417
-- Data for Name: sys_oper_log; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_oper_log VALUES (379, '操作日志', 9, 'com.basic.web.controller.monitor.SysOperlogController.clean()', 'DELETE', 1, 'admin', NULL, '/monitor/operlog/clean', '127.0.0.1', '内网IP', '{}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 14:17:16');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (380, '登录日志', 9, 'com.basic.web.controller.monitor.SysLogininforController.clean()', 'DELETE', 1, 'admin', NULL, '/monitor/logininfor/clean', '127.0.0.1', '内网IP', '{}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 14:17:20');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (381, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1754778882690195458', '127.0.0.1', '内网IP', '{menuId=1754778882690195458}', '{"msg":"存在子菜单,不允许删除","code":500}', 0, NULL, '2026-10-14 14:17:33');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (382, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1064', '127.0.0.1', '内网IP', '{menuId=1064}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 14:17:41');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (383, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1955896250820567041', '127.0.0.1', '内网IP', '{menuId=1955896250820567041}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:06:47');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (384, '菜单管理', 2, 'com.basic.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":null,"createTime":"2023-02-28 17:09:30","updateBy":"admin","updateTime":null,"remark":null,"params":{},"menuId":2,"menuName":"系统监控","parentName":null,"parentId":0,"orderNum":2,"path":"monitor","component":null,"query":"","isFrame":"1","isCache":"0","menuType":"M","visible":"0","status":"0","perms":"","icon":"monitor","children":[]}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:06:56');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (385, '菜单管理', 2, 'com.basic.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":null,"createTime":"2023-02-28 17:09:30","updateBy":"admin","updateTime":null,"remark":null,"params":{},"menuId":2,"menuName":"系统监控","parentName":null,"parentId":0,"orderNum":2,"path":"monitor","component":null,"query":"","isFrame":"1","isCache":"0","menuType":"M","visible":"1","status":"1","perms":"","icon":"monitor","children":[]}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:09');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (386, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753950971045343233', '127.0.0.1', '内网IP', '{menuId=1753950971045343233}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:24');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (387, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796354', '127.0.0.1', '内网IP', '{menuId=1753952621629796354}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:26');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (388, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796355', '127.0.0.1', '内网IP', '{menuId=1753952621629796355}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:30');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (389, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796356', '127.0.0.1', '内网IP', '{menuId=1753952621629796356}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:32');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (390, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796357', '127.0.0.1', '内网IP', '{menuId=1753952621629796357}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:34');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (391, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796358', '127.0.0.1', '内网IP', '{menuId=1753952621629796358}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:37');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (392, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1061', '127.0.0.1', '内网IP', '{menuId=1061}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:39');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (393, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1754778882690195458', '127.0.0.1', '内网IP', '{menuId=1754778882690195458}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:42');
INSERT INTO basicdb_flowable8.sys_oper_log VALUES (394, '参数管理', 2, 'com.basic.web.controller.system.SysConfigController.edit()', 'PUT', 1, 'admin', NULL, '/system/config', '127.0.0.1', '内网IP', '{"configId":4,"configKey":"sys.account.captchaOnOff","configName":"账号自助-验证码开关","configType":"Y","configValue":"true","createBy":"admin","createTime":"2023-02-28 17:09:30","params":{},"remark":"是否开启验证码功能（true开启，false关闭）","searchValue":null,"updateBy":"admin","updateTime":"2024-01-31 17:09:54"}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-17 19:20:21');


--
-- TOC entry 4748 (class 0 OID 4489856)
-- Dependencies: 419
-- Data for Name: sys_post; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_post VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2023-02-28 17:09:30', '', NULL, '');


--
-- TOC entry 4750 (class 0 OID 4489862)
-- Dependencies: 421
-- Data for Name: sys_role; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_role VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '超级管理员');
INSERT INTO basicdb_flowable8.sys_role VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-01-24 16:28:19', '普通角色');
INSERT INTO basicdb_flowable8.sys_role VALUES (4, '333', 'hgf', 0, '1', 1, 1, '0', '2', 'admin', '2024-02-06 14:00:18', NULL, NULL, NULL);
INSERT INTO basicdb_flowable8.sys_role VALUES (5, '用户1', 'usss', 3, '1', 1, 1, '0', '2', 'admin', '2024-02-06 16:52:25', NULL, NULL, NULL);


--
-- TOC entry 4751 (class 0 OID 4489871)
-- Dependencies: 422
-- Data for Name: sys_role_dept; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_role_dept VALUES (2, 100);
INSERT INTO basicdb_flowable8.sys_role_dept VALUES (2, 101);
INSERT INTO basicdb_flowable8.sys_role_dept VALUES (2, 105);


--
-- TOC entry 4752 (class 0 OID 4489874)
-- Dependencies: 423
-- Data for Name: sys_role_menu; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 105);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 100);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1001);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1002);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1003);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1004);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1005);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1006);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1007);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 101);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1008);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1009);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1010);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1011);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1012);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 102);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1013);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1014);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1015);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1016);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 103);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1017);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1018);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1019);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1020);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 104);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1021);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1022);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1023);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1024);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1025);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1026);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 106);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 107);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 108);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 500);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 501);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 2);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 109);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 110);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 111);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 112);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 113);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 3);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 114);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 115);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 116);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1561);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1500);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1501);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1502);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1503);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1504);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1505);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1506);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1507);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1509);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1510);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1530);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1531);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1532);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1533);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1540);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1541);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1542);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1543);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1544);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1560);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1562);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1563);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1570);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1571);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1572);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1573);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1574);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1575);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1576);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1577);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1580);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1581);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1582);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1583);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1699);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1511);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1500);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1511);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1501);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1570);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1571);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1572);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1573);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1574);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1575);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1576);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1502);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1503);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1577);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1504);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1505);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1506);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1601);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1530);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1602);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1531);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1603);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1532);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1533);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1507);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1540);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1541);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1542);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1543);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1544);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1615);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1616);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1617);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1618);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1619);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1620);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1621);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1622);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1623);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1624);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1625);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1509);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1560);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1561);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1562);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1563);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1608);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1609);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1610);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1611);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1612);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1613);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1614);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1678);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1679);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1680);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1681);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1510);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1580);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1581);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1582);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1583);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1606);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1607);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1699);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1626);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1627);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1628);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1629);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1630);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1631);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1632);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1633);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1634);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1635);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1636);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1637);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1638);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1639);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1640);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1641);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1642);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1643);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1644);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1645);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1646);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1647);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1648);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1649);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1650);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1651);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1652);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1653);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1654);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1655);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1656);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1657);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1658);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1659);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1660);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1661);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1662);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1663);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1664);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1665);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1666);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1667);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1668);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1669);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1670);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1671);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1672);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1673);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1674);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1675);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1676);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1677);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1682);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1683);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1684);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1685);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1686);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1687);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1688);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1689);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1690);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1691);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1692);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1693);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1692);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1693);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (1, 1694);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (2, 1694);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (4, 1694);
INSERT INTO basicdb_flowable8.sys_role_menu VALUES (5, 1694);


--
-- TOC entry 4754 (class 0 OID 4489878)
-- Dependencies: 425
-- Data for Name: sys_user; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_user VALUES (9, NULL, 'test002', '测试2', '00', NULL, NULL, '0', NULL, '$2a$10$udfBxRm0omFl7XpSmBk7/.wfsRfzP5qqc0EJ6bM0s01fgmoSiDIBq', '0', '0', '127.0.0.1', '2024-02-21 17:18:01', 'admin', '2024-02-21 17:17:44', NULL, '2024-02-21 17:18:00', NULL, NULL);
INSERT INTO basicdb_flowable8.sys_user VALUES (4, 101, 'xiaoan', '安全管理员', '00', 'xiaoan@email.com', '19999999999', '0', '/profile/avatar/2024/04/22/blob_20240422160106A001.jpeg', '$2a$10$So1TNdFpzE1dB09Z0eQs1OzFpFLMrWxbYtfTvxEFLMC/8SXTUPPEK', '0', '0', '127.0.0.1', '2024-10-11 10:13:43', 'admin', '2024-01-26 15:07:37', 'admin', '2024-10-11 10:13:42', '安全管理员', '2024-10-11 10:13:27');
INSERT INTO basicdb_flowable8.sys_user VALUES (6, NULL, '13249230230', 'AA', '00', NULL, NULL, '0', NULL, '$2a$10$.3MLA6zZE6RPDLzSx6jzseXhe9ShBXBgddJ5Z09Bp1immgkZ978JS', '0', '0', '192.168.170.21', '2024-02-06 15:51:51', 'admin', '2024-01-26 15:24:02', 'admin', '2024-02-06 15:51:50', NULL, '2024-02-21 11:49:28');
INSERT INTO basicdb_flowable8.sys_user VALUES (5, 100, 'xiaoxi', '系统管理员', '00', 'xiaoxi@email.com', '13333333333', '0', NULL, '$2a$10$FNM2ZPI/XwzY.arKiLUZtusSs9ui1X0xU3dfdcvLr.mb9eNskz22O', '0', '0', '192.168.170.21', '2024-04-24 09:12:57', 'admin', '2024-01-26 15:10:01', 'admin', '2024-04-24 09:12:56', '系统管理员', '2024-04-22 16:03:29');
INSERT INTO basicdb_flowable8.sys_user VALUES (7, NULL, '用户1', 'user1', '00', NULL, NULL, '0', NULL, '$2a$10$wfJ4zAYQ0kaZyp2XRyLO7.5bnloK3/M0HiOatf3kRWt//u2KMUNdu', '0', '2', NULL, NULL, 'admin', '2024-02-06 16:51:53', NULL, NULL, NULL, '2024-02-21 11:49:28');
INSERT INTO basicdb_flowable8.sys_user VALUES (2, 105, 'xiaoshen', '审计管理员', '00', 'xiaoshen@qq.com', '17777777777', '1', '/profile/avatar/2024/02/06/blob_20240206154427A003.jpeg', '$2a$10$MpBkNRsOzwiy54C/Vx8JoOjk6NV3Zz.jws030m1DMvguCcPP4OLJ6', '0', '0', '192.168.170.21', '2024-02-07 08:21:20', 'admin', '2023-02-28 17:09:30', 'admin', '2024-02-07 08:21:19', '审计管理员', '2024-02-21 11:49:28');
INSERT INTO basicdb_flowable8.sys_user VALUES (8, NULL, 'test001', '测试', '00', NULL, NULL, '0', NULL, '$2a$10$RAs8Dzmmt.UUD4JZvLX4Wes1Eblh0Z/OpQceqseQk78zhNvI62XvK', '0', '0', '127.0.0.1', '2025-08-14 10:43:36', 'admin', '2024-02-21 17:09:51', 'admin', '2025-08-14 10:43:36', NULL, '2025-08-14 10:43:21');
INSERT INTO basicdb_flowable8.sys_user VALUES (1, 100, 'admin', '超*管', '00', 'xiaochao@163.com', '18888888888', '1', '/profile/avatar/2024/02/06/blob_20240206154306A002.jpeg', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2026-10-17 21:07:10', 'admin', '2023-02-28 17:09:30', '', '2026-10-17 21:07:11', '管理员', '2024-02-21 11:49:28');


--
-- TOC entry 4755 (class 0 OID 4489887)
-- Dependencies: 426
-- Data for Name: sys_user_post; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_user_post VALUES (1, 1);


--
-- TOC entry 4756 (class 0 OID 4489890)
-- Dependencies: 427
-- Data for Name: sys_user_role; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: postgres
--

INSERT INTO basicdb_flowable8.sys_user_role VALUES (1, 1);
INSERT INTO basicdb_flowable8.sys_user_role VALUES (4, 2);
INSERT INTO basicdb_flowable8.sys_user_role VALUES (2, 2);
INSERT INTO basicdb_flowable8.sys_user_role VALUES (5, 2);
INSERT INTO basicdb_flowable8.sys_user_role VALUES (8, 2);
INSERT INTO basicdb_flowable8.sys_user_role VALUES (9, 2);


--
-- TOC entry 4814 (class 0 OID 4499720)
-- Dependencies: 485
-- Data for Name: tbl_flow_extend_procinst; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4805 (class 0 OID 4497564)
-- Dependencies: 476
-- Data for Name: wf_category; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4806 (class 0 OID 4497577)
-- Dependencies: 477
-- Data for Name: wf_category_org_link; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4807 (class 0 OID 4497582)
-- Dependencies: 478
-- Data for Name: wf_comment_info; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4808 (class 0 OID 4497601)
-- Dependencies: 479
-- Data for Name: wf_copy; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4809 (class 0 OID 4497621)
-- Dependencies: 480
-- Data for Name: wf_deploy_form; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4810 (class 0 OID 4497633)
-- Dependencies: 481
-- Data for Name: wf_form; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--

INSERT INTO basicdb_flowable8.wf_form VALUES ('67e67ce5edd9e70282102b6bc9f991a1', 'cs', 'cs', NULL, 'custom', NULL, 1, '', '0', 'admin', '2026-10-17 21:25:21.038', NULL, NULL, '0');


--
-- TOC entry 4811 (class 0 OID 4497648)
-- Dependencies: 482
-- Data for Name: wf_form_category; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4812 (class 0 OID 4497660)
-- Dependencies: 483
-- Data for Name: wf_form_template; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4813 (class 0 OID 4497671)
-- Dependencies: 484
-- Data for Name: wf_task_form; Type: TABLE DATA; Schema: basicdb_flowable8; Owner: system
--



--
-- TOC entry 4890 (class 0 OID 0)
-- Dependencies: 454
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: system
--

SELECT pg_catalog.setval('basicdb_flowable8.act_evt_log_log_nr__seq', 1, false);


--
-- TOC entry 4891 (class 0 OID 0)
-- Dependencies: 445
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: system
--

SELECT pg_catalog.setval('basicdb_flowable8.act_hi_tsk_log_id__seq', 1, false);


--
-- TOC entry 4892 (class 0 OID 0)
-- Dependencies: 379
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.gen_table_column_column_id_seq', 185, true);


--
-- TOC entry 4893 (class 0 OID 0)
-- Dependencies: 380
-- Name: gen_table_table_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: system
--

SELECT pg_catalog.setval('basicdb_flowable8.gen_table_table_id_seq', 18, true);


--
-- TOC entry 4894 (class 0 OID 0)
-- Dependencies: 395
-- Name: sys_config_config_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_config_config_id_seq', 13, true);


--
-- TOC entry 4895 (class 0 OID 0)
-- Dependencies: 398
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_dept_dept_id_seq', 111, true);


--
-- TOC entry 4896 (class 0 OID 0)
-- Dependencies: 400
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_dict_data_dict_code_seq', 32, true);


--
-- TOC entry 4897 (class 0 OID 0)
-- Dependencies: 402
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_dict_type_dict_id_seq', 12, true);


--
-- TOC entry 4898 (class 0 OID 0)
-- Dependencies: 406
-- Name: sys_job_job_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_job_job_id_seq', 4, true);


--
-- TOC entry 4899 (class 0 OID 0)
-- Dependencies: 408
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_job_log_job_log_id_seq', 1, false);


--
-- TOC entry 4900 (class 0 OID 0)
-- Dependencies: 410
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_logininfor_info_id_seq', 763, true);


--
-- TOC entry 4901 (class 0 OID 0)
-- Dependencies: 412
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_menu_menu_id_seq', 1066, true);


--
-- TOC entry 4902 (class 0 OID 0)
-- Dependencies: 414
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_notice_notice_id_seq', 3, true);


--
-- TOC entry 4903 (class 0 OID 0)
-- Dependencies: 416
-- Name: sys_notice_read_read_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: system
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_notice_read_read_id_seq', 1, false);


--
-- TOC entry 4904 (class 0 OID 0)
-- Dependencies: 418
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_oper_log_oper_id_seq', 394, true);


--
-- TOC entry 4905 (class 0 OID 0)
-- Dependencies: 420
-- Name: sys_post_post_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_post_post_id_seq', 5, true);


--
-- TOC entry 4906 (class 0 OID 0)
-- Dependencies: 424
-- Name: sys_role_role_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_role_role_id_seq', 5, true);


--
-- TOC entry 4907 (class 0 OID 0)
-- Dependencies: 428
-- Name: sys_user_user_id_seq; Type: SEQUENCE SET; Schema: basicdb_flowable8; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_flowable8.sys_user_user_id_seq', 9, true);


--
-- TOC entry 4480 (class 2606 OID 4490704)
-- Name: flw_channel_definition FLW_CHANNEL_DEFINITION_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.flw_channel_definition
    ADD CONSTRAINT "FLW_CHANNEL_DEFINITION_pkey" PRIMARY KEY (id_);


--
-- TOC entry 4477 (class 2606 OID 4490690)
-- Name: flw_event_definition FLW_EVENT_DEFINITION_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.flw_event_definition
    ADD CONSTRAINT "FLW_EVENT_DEFINITION_pkey" PRIMARY KEY (id_);


--
-- TOC entry 4472 (class 2606 OID 4490676)
-- Name: flw_event_deployment FLW_EVENT_DEPLOYMENT_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.flw_event_deployment
    ADD CONSTRAINT "FLW_EVENT_DEPLOYMENT_pkey" PRIMARY KEY (id_);


--
-- TOC entry 4474 (class 2606 OID 4490683)
-- Name: flw_event_resource FLW_EVENT_RESOURCE_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.flw_event_resource
    ADD CONSTRAINT "FLW_EVENT_RESOURCE_pkey" PRIMARY KEY (id_);


--
-- TOC entry 4182 (class 2606 OID 4489910)
-- Name: gen_table PRIMARY; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.gen_table
    ADD CONSTRAINT "PRIMARY" PRIMARY KEY (table_id);


--
-- TOC entry 4184 (class 2606 OID 4489912)
-- Name: gen_table_column PRIMARY00000; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.gen_table_column
    ADD CONSTRAINT "PRIMARY00000" PRIMARY KEY (column_id);


--
-- TOC entry 4186 (class 2606 OID 4489914)
-- Name: qrtz_blob_triggers PRIMARY00001; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.qrtz_blob_triggers
    ADD CONSTRAINT "PRIMARY00001" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4188 (class 2606 OID 4489916)
-- Name: qrtz_calendars PRIMARY00002; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.qrtz_calendars
    ADD CONSTRAINT "PRIMARY00002" PRIMARY KEY (sched_name, calendar_name);


--
-- TOC entry 4190 (class 2606 OID 4489918)
-- Name: qrtz_cron_triggers PRIMARY00003; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.qrtz_cron_triggers
    ADD CONSTRAINT "PRIMARY00003" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4192 (class 2606 OID 4489920)
-- Name: qrtz_fired_triggers PRIMARY00004; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.qrtz_fired_triggers
    ADD CONSTRAINT "PRIMARY00004" PRIMARY KEY (sched_name, entry_id);


--
-- TOC entry 4194 (class 2606 OID 4489922)
-- Name: qrtz_job_details PRIMARY00005; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.qrtz_job_details
    ADD CONSTRAINT "PRIMARY00005" PRIMARY KEY (sched_name, job_name, job_group);


--
-- TOC entry 4196 (class 2606 OID 4489924)
-- Name: qrtz_locks PRIMARY00006; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.qrtz_locks
    ADD CONSTRAINT "PRIMARY00006" PRIMARY KEY (sched_name, lock_name);


--
-- TOC entry 4198 (class 2606 OID 4489926)
-- Name: qrtz_paused_trigger_grps PRIMARY00007; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.qrtz_paused_trigger_grps
    ADD CONSTRAINT "PRIMARY00007" PRIMARY KEY (sched_name, trigger_group);


--
-- TOC entry 4200 (class 2606 OID 4489928)
-- Name: qrtz_scheduler_state PRIMARY00008; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.qrtz_scheduler_state
    ADD CONSTRAINT "PRIMARY00008" PRIMARY KEY (sched_name, instance_name);


--
-- TOC entry 4202 (class 2606 OID 4489930)
-- Name: qrtz_simple_triggers PRIMARY00009; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.qrtz_simple_triggers
    ADD CONSTRAINT "PRIMARY00009" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4204 (class 2606 OID 4489932)
-- Name: qrtz_simprop_triggers PRIMARY00010; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.qrtz_simprop_triggers
    ADD CONSTRAINT "PRIMARY00010" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4206 (class 2606 OID 4489934)
-- Name: qrtz_triggers PRIMARY00011; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.qrtz_triggers
    ADD CONSTRAINT "PRIMARY00011" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4209 (class 2606 OID 4489936)
-- Name: sys_config PRIMARY00012; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_config
    ADD CONSTRAINT "PRIMARY00012" PRIMARY KEY (config_id);


--
-- TOC entry 4213 (class 2606 OID 4489938)
-- Name: sys_dept PRIMARY00013; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_dept
    ADD CONSTRAINT "PRIMARY00013" PRIMARY KEY (dept_id);


--
-- TOC entry 4215 (class 2606 OID 4489940)
-- Name: sys_dict_data PRIMARY00014; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_dict_data
    ADD CONSTRAINT "PRIMARY00014" PRIMARY KEY (dict_code);


--
-- TOC entry 4217 (class 2606 OID 4489942)
-- Name: sys_dict_type PRIMARY00015; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_dict_type
    ADD CONSTRAINT "PRIMARY00015" PRIMARY KEY (dict_id);


--
-- TOC entry 4222 (class 2606 OID 4489944)
-- Name: sys_job PRIMARY00016; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_job
    ADD CONSTRAINT "PRIMARY00016" PRIMARY KEY (job_id, job_name, job_group);


--
-- TOC entry 4224 (class 2606 OID 4489946)
-- Name: sys_job_log PRIMARY00017; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_job_log
    ADD CONSTRAINT "PRIMARY00017" PRIMARY KEY (job_log_id);


--
-- TOC entry 4226 (class 2606 OID 4489948)
-- Name: sys_logininfor PRIMARY00018; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_logininfor
    ADD CONSTRAINT "PRIMARY00018" PRIMARY KEY (info_id);


--
-- TOC entry 4228 (class 2606 OID 4489950)
-- Name: sys_menu PRIMARY00019; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_menu
    ADD CONSTRAINT "PRIMARY00019" PRIMARY KEY (menu_id);


--
-- TOC entry 4230 (class 2606 OID 4489952)
-- Name: sys_notice PRIMARY00020; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_notice
    ADD CONSTRAINT "PRIMARY00020" PRIMARY KEY (notice_id);


--
-- TOC entry 4235 (class 2606 OID 4489954)
-- Name: sys_oper_log PRIMARY00021; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_oper_log
    ADD CONSTRAINT "PRIMARY00021" PRIMARY KEY (oper_id);


--
-- TOC entry 4237 (class 2606 OID 4489956)
-- Name: sys_post PRIMARY00022; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_post
    ADD CONSTRAINT "PRIMARY00022" PRIMARY KEY (post_id);


--
-- TOC entry 4239 (class 2606 OID 4489958)
-- Name: sys_role PRIMARY00023; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_role
    ADD CONSTRAINT "PRIMARY00023" PRIMARY KEY (role_id);


--
-- TOC entry 4241 (class 2606 OID 4489960)
-- Name: sys_role_dept PRIMARY00024; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_role_dept
    ADD CONSTRAINT "PRIMARY00024" PRIMARY KEY (role_id, dept_id);


--
-- TOC entry 4243 (class 2606 OID 4489962)
-- Name: sys_role_menu PRIMARY00025; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_role_menu
    ADD CONSTRAINT "PRIMARY00025" PRIMARY KEY (role_id, menu_id);


--
-- TOC entry 4245 (class 2606 OID 4489964)
-- Name: sys_user PRIMARY00026; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_user
    ADD CONSTRAINT "PRIMARY00026" PRIMARY KEY (user_id);


--
-- TOC entry 4247 (class 2606 OID 4489966)
-- Name: sys_user_post PRIMARY00027; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_user_post
    ADD CONSTRAINT "PRIMARY00027" PRIMARY KEY (user_id, post_id);


--
-- TOC entry 4249 (class 2606 OID 4489968)
-- Name: sys_user_role PRIMARY00028; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: postgres
--

ALTER TABLE ONLY basicdb_flowable8.sys_user_role
    ADD CONSTRAINT "PRIMARY00028" PRIMARY KEY (user_id, role_id);


--
-- TOC entry 4406 (class 2606 OID 4490315)
-- Name: act_evt_log act_evt_log_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_evt_log
    ADD CONSTRAINT act_evt_log_pkey PRIMARY KEY (log_nr_);


--
-- TOC entry 4253 (class 2606 OID 4490003)
-- Name: act_ge_bytearray act_ge_bytearray_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ge_bytearray
    ADD CONSTRAINT act_ge_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 4251 (class 2606 OID 4489996)
-- Name: act_ge_property act_ge_property_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ge_property
    ADD CONSTRAINT act_ge_property_pkey PRIMARY KEY (name_);


--
-- TOC entry 4430 (class 2606 OID 4490549)
-- Name: act_hi_actinst act_hi_actinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_hi_actinst
    ADD CONSTRAINT act_hi_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4445 (class 2606 OID 4490570)
-- Name: act_hi_attachment act_hi_attachment_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_hi_attachment
    ADD CONSTRAINT act_hi_attachment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4443 (class 2606 OID 4490563)
-- Name: act_hi_comment act_hi_comment_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_hi_comment
    ADD CONSTRAINT act_hi_comment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4436 (class 2606 OID 4490556)
-- Name: act_hi_detail act_hi_detail_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_hi_detail
    ADD CONSTRAINT act_hi_detail_pkey PRIMARY KEY (id_);


--
-- TOC entry 4262 (class 2606 OID 4490021)
-- Name: act_hi_entitylink act_hi_entitylink_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_hi_entitylink
    ADD CONSTRAINT act_hi_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4278 (class 2606 OID 4490044)
-- Name: act_hi_identitylink act_hi_identitylink_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_hi_identitylink
    ADD CONSTRAINT act_hi_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4423 (class 2606 OID 4490538)
-- Name: act_hi_procinst act_hi_procinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4425 (class 2606 OID 4490540)
-- Name: act_hi_procinst act_hi_procinst_proc_inst_id__key; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_proc_inst_id__key UNIQUE (proc_inst_id_);


--
-- TOC entry 4355 (class 2606 OID 4490220)
-- Name: act_hi_taskinst act_hi_taskinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_hi_taskinst
    ADD CONSTRAINT act_hi_taskinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4361 (class 2606 OID 4490230)
-- Name: act_hi_tsk_log act_hi_tsk_log_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_hi_tsk_log
    ADD CONSTRAINT act_hi_tsk_log_pkey PRIMARY KEY (id_);


--
-- TOC entry 4372 (class 2606 OID 4490257)
-- Name: act_hi_varinst act_hi_varinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_hi_varinst
    ADD CONSTRAINT act_hi_varinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4449 (class 2606 OID 4490601)
-- Name: act_id_bytearray act_id_bytearray_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_bytearray
    ADD CONSTRAINT act_id_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 4451 (class 2606 OID 4490608)
-- Name: act_id_group act_id_group_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_group
    ADD CONSTRAINT act_id_group_pkey PRIMARY KEY (id_);


--
-- TOC entry 4459 (class 2606 OID 4490628)
-- Name: act_id_info act_id_info_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_info
    ADD CONSTRAINT act_id_info_pkey PRIMARY KEY (id_);


--
-- TOC entry 4453 (class 2606 OID 4490613)
-- Name: act_id_membership act_id_membership_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_membership
    ADD CONSTRAINT act_id_membership_pkey PRIMARY KEY (user_id_, group_id_);


--
-- TOC entry 4467 (class 2606 OID 4490647)
-- Name: act_id_priv_mapping act_id_priv_mapping_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_priv_mapping
    ADD CONSTRAINT act_id_priv_mapping_pkey PRIMARY KEY (id_);


--
-- TOC entry 4463 (class 2606 OID 4490640)
-- Name: act_id_priv act_id_priv_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_priv
    ADD CONSTRAINT act_id_priv_pkey PRIMARY KEY (id_);


--
-- TOC entry 4447 (class 2606 OID 4490594)
-- Name: act_id_property act_id_property_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_property
    ADD CONSTRAINT act_id_property_pkey PRIMARY KEY (name_);


--
-- TOC entry 4461 (class 2606 OID 4490635)
-- Name: act_id_token act_id_token_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_token
    ADD CONSTRAINT act_id_token_pkey PRIMARY KEY (id_);


--
-- TOC entry 4457 (class 2606 OID 4490621)
-- Name: act_id_user act_id_user_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_user
    ADD CONSTRAINT act_id_user_pkey PRIMARY KEY (id_);


--
-- TOC entry 4410 (class 2606 OID 4490320)
-- Name: act_procdef_info act_procdef_info_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_procdef_info
    ADD CONSTRAINT act_procdef_info_pkey PRIMARY KEY (id_);


--
-- TOC entry 4386 (class 2606 OID 4490280)
-- Name: act_re_deployment act_re_deployment_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_re_deployment
    ADD CONSTRAINT act_re_deployment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4391 (class 2606 OID 4490288)
-- Name: act_re_model act_re_model_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_re_model
    ADD CONSTRAINT act_re_model_pkey PRIMARY KEY (id_);


--
-- TOC entry 4402 (class 2606 OID 4490305)
-- Name: act_re_procdef act_re_procdef_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_re_procdef
    ADD CONSTRAINT act_re_procdef_pkey PRIMARY KEY (id_);


--
-- TOC entry 4421 (class 2606 OID 4490329)
-- Name: act_ru_actinst act_ru_actinst_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_actinst
    ADD CONSTRAINT act_ru_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4329 (class 2606 OID 4490080)
-- Name: act_ru_deadletter_job act_ru_deadletter_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_deadletter_job
    ADD CONSTRAINT act_ru_deadletter_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4260 (class 2606 OID 4490010)
-- Name: act_ru_entitylink act_ru_entitylink_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_entitylink
    ADD CONSTRAINT act_ru_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4384 (class 2606 OID 4490268)
-- Name: act_ru_event_subscr act_ru_event_subscr_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_event_subscr
    ADD CONSTRAINT act_ru_event_subscr_pkey PRIMARY KEY (id_);


--
-- TOC entry 4400 (class 2606 OID 4490296)
-- Name: act_ru_execution act_ru_execution_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_execution
    ADD CONSTRAINT act_ru_execution_pkey PRIMARY KEY (id_);


--
-- TOC entry 4339 (class 2606 OID 4490096)
-- Name: act_ru_external_job act_ru_external_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_external_job
    ADD CONSTRAINT act_ru_external_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4331 (class 2606 OID 4490088)
-- Name: act_ru_history_job act_ru_history_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_history_job
    ADD CONSTRAINT act_ru_history_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4276 (class 2606 OID 4490032)
-- Name: act_ru_identitylink act_ru_identitylink_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_identitylink
    ADD CONSTRAINT act_ru_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4295 (class 2606 OID 4490056)
-- Name: act_ru_job act_ru_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_job
    ADD CONSTRAINT act_ru_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4318 (class 2606 OID 4490072)
-- Name: act_ru_suspended_job act_ru_suspended_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_suspended_job
    ADD CONSTRAINT act_ru_suspended_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4353 (class 2606 OID 4490207)
-- Name: act_ru_task act_ru_task_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_task
    ADD CONSTRAINT act_ru_task_pkey PRIMARY KEY (id_);


--
-- TOC entry 4307 (class 2606 OID 4490064)
-- Name: act_ru_timer_job act_ru_timer_job_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_timer_job
    ADD CONSTRAINT act_ru_timer_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4370 (class 2606 OID 4490241)
-- Name: act_ru_variable act_ru_variable_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_variable
    ADD CONSTRAINT act_ru_variable_pkey PRIMARY KEY (id_);


--
-- TOC entry 4412 (class 2606 OID 4490529)
-- Name: act_procdef_info act_uniq_info_procdef; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_procdef_info
    ADD CONSTRAINT act_uniq_info_procdef UNIQUE (proc_def_id_);


--
-- TOC entry 4465 (class 2606 OID 4490669)
-- Name: act_id_priv act_uniq_priv_name; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_priv
    ADD CONSTRAINT act_uniq_priv_name UNIQUE (name_);


--
-- TOC entry 4404 (class 2606 OID 4490348)
-- Name: act_re_procdef act_uniq_procdef; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_re_procdef
    ADD CONSTRAINT act_uniq_procdef UNIQUE (key_, version_, derived_version_, tenant_id_);


--
-- TOC entry 4344 (class 2606 OID 4490193)
-- Name: flw_ru_batch_part flw_ru_batch_part_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.flw_ru_batch_part
    ADD CONSTRAINT flw_ru_batch_part_pkey PRIMARY KEY (id_);


--
-- TOC entry 4341 (class 2606 OID 4490185)
-- Name: flw_ru_batch flw_ru_batch_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.flw_ru_batch
    ADD CONSTRAINT flw_ru_batch_pkey PRIMARY KEY (id_);


--
-- TOC entry 4512 (class 2606 OID 4499754)
-- Name: flw_expression pk_flw_expression; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.flw_expression
    ADD CONSTRAINT pk_flw_expression PRIMARY KEY (id);


--
-- TOC entry 4510 (class 2606 OID 4499738)
-- Name: tbl_flow_extend_procinst pk_tbl_flow_extend_procinst; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.tbl_flow_extend_procinst
    ADD CONSTRAINT pk_tbl_flow_extend_procinst PRIMARY KEY (id);


--
-- TOC entry 4483 (class 2606 OID 4499654)
-- Name: wf_category pk_wf_category; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.wf_category
    ADD CONSTRAINT pk_wf_category PRIMARY KEY (category_id);


--
-- TOC entry 4486 (class 2606 OID 4499656)
-- Name: wf_comment_info pk_wf_comment_info; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.wf_comment_info
    ADD CONSTRAINT pk_wf_comment_info PRIMARY KEY (id);


--
-- TOC entry 4489 (class 2606 OID 4499658)
-- Name: wf_copy pk_wf_copy; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.wf_copy
    ADD CONSTRAINT pk_wf_copy PRIMARY KEY (copy_id);


--
-- TOC entry 4494 (class 2606 OID 4499650)
-- Name: wf_deploy_form pk_wf_deploy_form; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.wf_deploy_form
    ADD CONSTRAINT pk_wf_deploy_form PRIMARY KEY (id);


--
-- TOC entry 4496 (class 2606 OID 4499644)
-- Name: wf_form pk_wf_form; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.wf_form
    ADD CONSTRAINT pk_wf_form PRIMARY KEY (form_id);


--
-- TOC entry 4499 (class 2606 OID 4499646)
-- Name: wf_form_category pk_wf_form_category; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.wf_form_category
    ADD CONSTRAINT pk_wf_form_category PRIMARY KEY (id);


--
-- TOC entry 4501 (class 2606 OID 4499648)
-- Name: wf_form_template pk_wf_form_template; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.wf_form_template
    ADD CONSTRAINT pk_wf_form_template PRIMARY KEY (form_id);


--
-- TOC entry 4506 (class 2606 OID 4499652)
-- Name: wf_task_form pk_wf_task_form; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.wf_task_form
    ADD CONSTRAINT pk_wf_task_form PRIMARY KEY (id);


--
-- TOC entry 4211 (class 2606 OID 4489970)
-- Name: sys_demo sys_demo_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.sys_demo
    ADD CONSTRAINT sys_demo_pkey PRIMARY KEY (id);


--
-- TOC entry 4220 (class 2606 OID 4489972)
-- Name: sys_files sys_files_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.sys_files
    ADD CONSTRAINT sys_files_pkey PRIMARY KEY (id);


--
-- TOC entry 4232 (class 2606 OID 4489974)
-- Name: sys_notice_read sys_notice_read_pkey; Type: CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.sys_notice_read
    ADD CONSTRAINT sys_notice_read_pkey PRIMARY KEY (read_id);


--
-- TOC entry 4362 (class 1259 OID 4490234)
-- Name: act_idx_act_hi_tsk_log_task; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_act_hi_tsk_log_task ON basicdb_flowable8.act_hi_tsk_log USING btree (task_id_);


--
-- TOC entry 4267 (class 1259 OID 4490379)
-- Name: act_idx_athrz_procedef; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_athrz_procedef ON basicdb_flowable8.act_ru_identitylink USING btree (proc_def_id_);


--
-- TOC entry 4254 (class 1259 OID 4490334)
-- Name: act_idx_bytear_depl; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_bytear_depl ON basicdb_flowable8.act_ge_bytearray USING btree (deployment_id_);


--
-- TOC entry 4481 (class 1259 OID 4490705)
-- Name: act_idx_channel_def_uniq; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE UNIQUE INDEX act_idx_channel_def_uniq ON basicdb_flowable8.flw_channel_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 4319 (class 1259 OID 4490109)
-- Name: act_idx_deadletter_job_correlation_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_deadletter_job_correlation_id ON basicdb_flowable8.act_ru_deadletter_job USING btree (correlation_id_);


--
-- TOC entry 4320 (class 1259 OID 4490108)
-- Name: act_idx_deadletter_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_deadletter_job_custom_values_id ON basicdb_flowable8.act_ru_deadletter_job USING btree (custom_values_id_);


--
-- TOC entry 4321 (class 1259 OID 4490107)
-- Name: act_idx_deadletter_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_deadletter_job_exception_stack_id ON basicdb_flowable8.act_ru_deadletter_job USING btree (exception_stack_id_);


--
-- TOC entry 4322 (class 1259 OID 4490475)
-- Name: act_idx_deadletter_job_execution_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_deadletter_job_execution_id ON basicdb_flowable8.act_ru_deadletter_job USING btree (execution_id_);


--
-- TOC entry 4323 (class 1259 OID 4490487)
-- Name: act_idx_deadletter_job_proc_def_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_deadletter_job_proc_def_id ON basicdb_flowable8.act_ru_deadletter_job USING btree (proc_def_id_);


--
-- TOC entry 4324 (class 1259 OID 4490481)
-- Name: act_idx_deadletter_job_process_instance_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_deadletter_job_process_instance_id ON basicdb_flowable8.act_ru_deadletter_job USING btree (process_instance_id_);


--
-- TOC entry 4325 (class 1259 OID 4490172)
-- Name: act_idx_djob_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_djob_scope ON basicdb_flowable8.act_ru_deadletter_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4326 (class 1259 OID 4490174)
-- Name: act_idx_djob_scope_def; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_djob_scope_def ON basicdb_flowable8.act_ru_deadletter_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4327 (class 1259 OID 4490173)
-- Name: act_idx_djob_sub_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_djob_sub_scope ON basicdb_flowable8.act_ru_deadletter_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4332 (class 1259 OID 4490175)
-- Name: act_idx_ejob_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ejob_scope ON basicdb_flowable8.act_ru_external_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4333 (class 1259 OID 4490177)
-- Name: act_idx_ejob_scope_def; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ejob_scope_def ON basicdb_flowable8.act_ru_external_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4334 (class 1259 OID 4490176)
-- Name: act_idx_ejob_sub_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ejob_sub_scope ON basicdb_flowable8.act_ru_external_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4255 (class 1259 OID 4490012)
-- Name: act_idx_ent_lnk_ref_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ent_lnk_ref_scope ON basicdb_flowable8.act_ru_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 4256 (class 1259 OID 4490013)
-- Name: act_idx_ent_lnk_root_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ent_lnk_root_scope ON basicdb_flowable8.act_ru_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 4257 (class 1259 OID 4490011)
-- Name: act_idx_ent_lnk_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ent_lnk_scope ON basicdb_flowable8.act_ru_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 4258 (class 1259 OID 4490014)
-- Name: act_idx_ent_lnk_scope_def; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ent_lnk_scope_def ON basicdb_flowable8.act_ru_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 4478 (class 1259 OID 4490697)
-- Name: act_idx_event_def_uniq; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE UNIQUE INDEX act_idx_event_def_uniq ON basicdb_flowable8.flw_event_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 4379 (class 1259 OID 4490270)
-- Name: act_idx_event_subscr; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_event_subscr ON basicdb_flowable8.act_ru_event_subscr USING btree (execution_id_);


--
-- TOC entry 4380 (class 1259 OID 4490269)
-- Name: act_idx_event_subscr_config_; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_event_subscr_config_ ON basicdb_flowable8.act_ru_event_subscr USING btree (configuration_);


--
-- TOC entry 4381 (class 1259 OID 4490271)
-- Name: act_idx_event_subscr_proc_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_event_subscr_proc_id ON basicdb_flowable8.act_ru_event_subscr USING btree (proc_inst_id_);


--
-- TOC entry 4382 (class 1259 OID 4490272)
-- Name: act_idx_event_subscr_scoperef_; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_event_subscr_scoperef_ ON basicdb_flowable8.act_ru_event_subscr USING btree (scope_id_, scope_type_);


--
-- TOC entry 4392 (class 1259 OID 4490355)
-- Name: act_idx_exe_parent; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_exe_parent ON basicdb_flowable8.act_ru_execution USING btree (parent_id_);


--
-- TOC entry 4393 (class 1259 OID 4490367)
-- Name: act_idx_exe_procdef; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_exe_procdef ON basicdb_flowable8.act_ru_execution USING btree (proc_def_id_);


--
-- TOC entry 4394 (class 1259 OID 4490349)
-- Name: act_idx_exe_procinst; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_exe_procinst ON basicdb_flowable8.act_ru_execution USING btree (proc_inst_id_);


--
-- TOC entry 4395 (class 1259 OID 4490331)
-- Name: act_idx_exe_root; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_exe_root ON basicdb_flowable8.act_ru_execution USING btree (root_proc_inst_id_);


--
-- TOC entry 4396 (class 1259 OID 4490361)
-- Name: act_idx_exe_super; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_exe_super ON basicdb_flowable8.act_ru_execution USING btree (super_exec_);


--
-- TOC entry 4397 (class 1259 OID 4490330)
-- Name: act_idx_exec_buskey; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_exec_buskey ON basicdb_flowable8.act_ru_execution USING btree (business_key_);


--
-- TOC entry 4398 (class 1259 OID 4490332)
-- Name: act_idx_exec_ref_id_; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_exec_ref_id_ ON basicdb_flowable8.act_ru_execution USING btree (reference_id_);


--
-- TOC entry 4335 (class 1259 OID 4490112)
-- Name: act_idx_external_job_correlation_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_external_job_correlation_id ON basicdb_flowable8.act_ru_external_job USING btree (correlation_id_);


--
-- TOC entry 4336 (class 1259 OID 4490111)
-- Name: act_idx_external_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_external_job_custom_values_id ON basicdb_flowable8.act_ru_external_job USING btree (custom_values_id_);


--
-- TOC entry 4337 (class 1259 OID 4490110)
-- Name: act_idx_external_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_external_job_exception_stack_id ON basicdb_flowable8.act_ru_external_job USING btree (exception_stack_id_);


--
-- TOC entry 4431 (class 1259 OID 4490575)
-- Name: act_idx_hi_act_inst_end; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_act_inst_end ON basicdb_flowable8.act_hi_actinst USING btree (end_time_);


--
-- TOC entry 4432 (class 1259 OID 4490585)
-- Name: act_idx_hi_act_inst_exec; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_act_inst_exec ON basicdb_flowable8.act_hi_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 4433 (class 1259 OID 4490584)
-- Name: act_idx_hi_act_inst_procinst; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_act_inst_procinst ON basicdb_flowable8.act_hi_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 4434 (class 1259 OID 4490574)
-- Name: act_idx_hi_act_inst_start; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_act_inst_start ON basicdb_flowable8.act_hi_actinst USING btree (start_time_);


--
-- TOC entry 4437 (class 1259 OID 4490577)
-- Name: act_idx_hi_detail_act_inst; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_detail_act_inst ON basicdb_flowable8.act_hi_detail USING btree (act_inst_id_);


--
-- TOC entry 4438 (class 1259 OID 4490579)
-- Name: act_idx_hi_detail_name; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_detail_name ON basicdb_flowable8.act_hi_detail USING btree (name_);


--
-- TOC entry 4439 (class 1259 OID 4490576)
-- Name: act_idx_hi_detail_proc_inst; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_detail_proc_inst ON basicdb_flowable8.act_hi_detail USING btree (proc_inst_id_);


--
-- TOC entry 4440 (class 1259 OID 4490580)
-- Name: act_idx_hi_detail_task_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_detail_task_id ON basicdb_flowable8.act_hi_detail USING btree (task_id_);


--
-- TOC entry 4441 (class 1259 OID 4490578)
-- Name: act_idx_hi_detail_time; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_detail_time ON basicdb_flowable8.act_hi_detail USING btree (time_);


--
-- TOC entry 4263 (class 1259 OID 4490023)
-- Name: act_idx_hi_ent_lnk_ref_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_ent_lnk_ref_scope ON basicdb_flowable8.act_hi_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 4264 (class 1259 OID 4490024)
-- Name: act_idx_hi_ent_lnk_root_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_ent_lnk_root_scope ON basicdb_flowable8.act_hi_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 4265 (class 1259 OID 4490022)
-- Name: act_idx_hi_ent_lnk_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_ent_lnk_scope ON basicdb_flowable8.act_hi_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 4266 (class 1259 OID 4490025)
-- Name: act_idx_hi_ent_lnk_scope_def; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_ent_lnk_scope_def ON basicdb_flowable8.act_hi_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 4279 (class 1259 OID 4490587)
-- Name: act_idx_hi_ident_lnk_procinst; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_procinst ON basicdb_flowable8.act_hi_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 4280 (class 1259 OID 4490046)
-- Name: act_idx_hi_ident_lnk_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_scope ON basicdb_flowable8.act_hi_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 4281 (class 1259 OID 4490048)
-- Name: act_idx_hi_ident_lnk_scope_def; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_scope_def ON basicdb_flowable8.act_hi_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4282 (class 1259 OID 4490047)
-- Name: act_idx_hi_ident_lnk_sub_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_sub_scope ON basicdb_flowable8.act_hi_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4283 (class 1259 OID 4490586)
-- Name: act_idx_hi_ident_lnk_task; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_task ON basicdb_flowable8.act_hi_identitylink USING btree (task_id_);


--
-- TOC entry 4284 (class 1259 OID 4490045)
-- Name: act_idx_hi_ident_lnk_user; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_ident_lnk_user ON basicdb_flowable8.act_hi_identitylink USING btree (user_id_);


--
-- TOC entry 4426 (class 1259 OID 4490572)
-- Name: act_idx_hi_pro_i_buskey; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_pro_i_buskey ON basicdb_flowable8.act_hi_procinst USING btree (business_key_);


--
-- TOC entry 4427 (class 1259 OID 4490571)
-- Name: act_idx_hi_pro_inst_end; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_pro_inst_end ON basicdb_flowable8.act_hi_procinst USING btree (end_time_);


--
-- TOC entry 4428 (class 1259 OID 4490573)
-- Name: act_idx_hi_pro_super_procinst; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_pro_super_procinst ON basicdb_flowable8.act_hi_procinst USING btree (super_process_instance_id_);


--
-- TOC entry 4373 (class 1259 OID 4490583)
-- Name: act_idx_hi_procvar_exe; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_procvar_exe ON basicdb_flowable8.act_hi_varinst USING btree (execution_id_);


--
-- TOC entry 4374 (class 1259 OID 4490258)
-- Name: act_idx_hi_procvar_name_type; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_procvar_name_type ON basicdb_flowable8.act_hi_varinst USING btree (name_, var_type_);


--
-- TOC entry 4375 (class 1259 OID 4490581)
-- Name: act_idx_hi_procvar_proc_inst; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_procvar_proc_inst ON basicdb_flowable8.act_hi_varinst USING btree (proc_inst_id_);


--
-- TOC entry 4376 (class 1259 OID 4490582)
-- Name: act_idx_hi_procvar_task_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_procvar_task_id ON basicdb_flowable8.act_hi_varinst USING btree (task_id_);


--
-- TOC entry 4356 (class 1259 OID 4490588)
-- Name: act_idx_hi_task_inst_procinst; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_task_inst_procinst ON basicdb_flowable8.act_hi_taskinst USING btree (proc_inst_id_);


--
-- TOC entry 4357 (class 1259 OID 4490231)
-- Name: act_idx_hi_task_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_task_scope ON basicdb_flowable8.act_hi_taskinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 4358 (class 1259 OID 4490233)
-- Name: act_idx_hi_task_scope_def; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_task_scope_def ON basicdb_flowable8.act_hi_taskinst USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4359 (class 1259 OID 4490232)
-- Name: act_idx_hi_task_sub_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_task_sub_scope ON basicdb_flowable8.act_hi_taskinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4377 (class 1259 OID 4490259)
-- Name: act_idx_hi_var_scope_id_type; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_var_scope_id_type ON basicdb_flowable8.act_hi_varinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 4378 (class 1259 OID 4490260)
-- Name: act_idx_hi_var_sub_id_type; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_hi_var_sub_id_type ON basicdb_flowable8.act_hi_varinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4268 (class 1259 OID 4490034)
-- Name: act_idx_ident_lnk_group; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ident_lnk_group ON basicdb_flowable8.act_ru_identitylink USING btree (group_id_);


--
-- TOC entry 4269 (class 1259 OID 4490035)
-- Name: act_idx_ident_lnk_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ident_lnk_scope ON basicdb_flowable8.act_ru_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 4270 (class 1259 OID 4490037)
-- Name: act_idx_ident_lnk_scope_def; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ident_lnk_scope_def ON basicdb_flowable8.act_ru_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4271 (class 1259 OID 4490036)
-- Name: act_idx_ident_lnk_sub_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ident_lnk_sub_scope ON basicdb_flowable8.act_ru_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4272 (class 1259 OID 4490033)
-- Name: act_idx_ident_lnk_user; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ident_lnk_user ON basicdb_flowable8.act_ru_identitylink USING btree (user_id_);


--
-- TOC entry 4273 (class 1259 OID 4490385)
-- Name: act_idx_idl_procinst; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_idl_procinst ON basicdb_flowable8.act_ru_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 4285 (class 1259 OID 4490099)
-- Name: act_idx_job_correlation_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_job_correlation_id ON basicdb_flowable8.act_ru_job USING btree (correlation_id_);


--
-- TOC entry 4286 (class 1259 OID 4490098)
-- Name: act_idx_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_job_custom_values_id ON basicdb_flowable8.act_ru_job USING btree (custom_values_id_);


--
-- TOC entry 4287 (class 1259 OID 4490097)
-- Name: act_idx_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_job_exception_stack_id ON basicdb_flowable8.act_ru_job USING btree (exception_stack_id_);


--
-- TOC entry 4288 (class 1259 OID 4490421)
-- Name: act_idx_job_execution_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_job_execution_id ON basicdb_flowable8.act_ru_job USING btree (execution_id_);


--
-- TOC entry 4289 (class 1259 OID 4490433)
-- Name: act_idx_job_proc_def_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_job_proc_def_id ON basicdb_flowable8.act_ru_job USING btree (proc_def_id_);


--
-- TOC entry 4290 (class 1259 OID 4490427)
-- Name: act_idx_job_process_instance_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_job_process_instance_id ON basicdb_flowable8.act_ru_job USING btree (process_instance_id_);


--
-- TOC entry 4291 (class 1259 OID 4490163)
-- Name: act_idx_job_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_job_scope ON basicdb_flowable8.act_ru_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4292 (class 1259 OID 4490165)
-- Name: act_idx_job_scope_def; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_job_scope_def ON basicdb_flowable8.act_ru_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4293 (class 1259 OID 4490164)
-- Name: act_idx_job_sub_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_job_sub_scope ON basicdb_flowable8.act_ru_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4454 (class 1259 OID 4490648)
-- Name: act_idx_memb_group; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_memb_group ON basicdb_flowable8.act_id_membership USING btree (group_id_);


--
-- TOC entry 4455 (class 1259 OID 4490654)
-- Name: act_idx_memb_user; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_memb_user ON basicdb_flowable8.act_id_membership USING btree (user_id_);


--
-- TOC entry 4387 (class 1259 OID 4490510)
-- Name: act_idx_model_deployment; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_model_deployment ON basicdb_flowable8.act_re_model USING btree (deployment_id_);


--
-- TOC entry 4388 (class 1259 OID 4490498)
-- Name: act_idx_model_source; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_model_source ON basicdb_flowable8.act_re_model USING btree (editor_source_value_id_);


--
-- TOC entry 4389 (class 1259 OID 4490504)
-- Name: act_idx_model_source_extra; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_model_source_extra ON basicdb_flowable8.act_re_model USING btree (editor_source_extra_value_id_);


--
-- TOC entry 4468 (class 1259 OID 4490667)
-- Name: act_idx_priv_group; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_priv_group ON basicdb_flowable8.act_id_priv_mapping USING btree (group_id_);


--
-- TOC entry 4469 (class 1259 OID 4490660)
-- Name: act_idx_priv_mapping; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_priv_mapping ON basicdb_flowable8.act_id_priv_mapping USING btree (priv_id_);


--
-- TOC entry 4470 (class 1259 OID 4490666)
-- Name: act_idx_priv_user; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_priv_user ON basicdb_flowable8.act_id_priv_mapping USING btree (user_id_);


--
-- TOC entry 4407 (class 1259 OID 4490516)
-- Name: act_idx_procdef_info_json; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_procdef_info_json ON basicdb_flowable8.act_procdef_info USING btree (info_json_id_);


--
-- TOC entry 4408 (class 1259 OID 4490522)
-- Name: act_idx_procdef_info_proc; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_procdef_info_proc ON basicdb_flowable8.act_procdef_info USING btree (proc_def_id_);


--
-- TOC entry 4413 (class 1259 OID 4490336)
-- Name: act_idx_ru_acti_end; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ru_acti_end ON basicdb_flowable8.act_ru_actinst USING btree (end_time_);


--
-- TOC entry 4414 (class 1259 OID 4490339)
-- Name: act_idx_ru_acti_exec; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ru_acti_exec ON basicdb_flowable8.act_ru_actinst USING btree (execution_id_);


--
-- TOC entry 4415 (class 1259 OID 4490340)
-- Name: act_idx_ru_acti_exec_act; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ru_acti_exec_act ON basicdb_flowable8.act_ru_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 4416 (class 1259 OID 4490337)
-- Name: act_idx_ru_acti_proc; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ru_acti_proc ON basicdb_flowable8.act_ru_actinst USING btree (proc_inst_id_);


--
-- TOC entry 4417 (class 1259 OID 4490338)
-- Name: act_idx_ru_acti_proc_act; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ru_acti_proc_act ON basicdb_flowable8.act_ru_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 4418 (class 1259 OID 4490335)
-- Name: act_idx_ru_acti_start; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ru_acti_start ON basicdb_flowable8.act_ru_actinst USING btree (start_time_);


--
-- TOC entry 4419 (class 1259 OID 4490341)
-- Name: act_idx_ru_acti_task; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ru_acti_task ON basicdb_flowable8.act_ru_actinst USING btree (task_id_);


--
-- TOC entry 4363 (class 1259 OID 4490242)
-- Name: act_idx_ru_var_scope_id_type; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ru_var_scope_id_type ON basicdb_flowable8.act_ru_variable USING btree (scope_id_, scope_type_);


--
-- TOC entry 4364 (class 1259 OID 4490243)
-- Name: act_idx_ru_var_sub_id_type; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_ru_var_sub_id_type ON basicdb_flowable8.act_ru_variable USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4308 (class 1259 OID 4490169)
-- Name: act_idx_sjob_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_sjob_scope ON basicdb_flowable8.act_ru_suspended_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4309 (class 1259 OID 4490171)
-- Name: act_idx_sjob_scope_def; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_sjob_scope_def ON basicdb_flowable8.act_ru_suspended_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4310 (class 1259 OID 4490170)
-- Name: act_idx_sjob_sub_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_sjob_sub_scope ON basicdb_flowable8.act_ru_suspended_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4311 (class 1259 OID 4490106)
-- Name: act_idx_suspended_job_correlation_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_suspended_job_correlation_id ON basicdb_flowable8.act_ru_suspended_job USING btree (correlation_id_);


--
-- TOC entry 4312 (class 1259 OID 4490105)
-- Name: act_idx_suspended_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_suspended_job_custom_values_id ON basicdb_flowable8.act_ru_suspended_job USING btree (custom_values_id_);


--
-- TOC entry 4313 (class 1259 OID 4490104)
-- Name: act_idx_suspended_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_suspended_job_exception_stack_id ON basicdb_flowable8.act_ru_suspended_job USING btree (exception_stack_id_);


--
-- TOC entry 4314 (class 1259 OID 4490457)
-- Name: act_idx_suspended_job_execution_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_suspended_job_execution_id ON basicdb_flowable8.act_ru_suspended_job USING btree (execution_id_);


--
-- TOC entry 4315 (class 1259 OID 4490469)
-- Name: act_idx_suspended_job_proc_def_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_suspended_job_proc_def_id ON basicdb_flowable8.act_ru_suspended_job USING btree (proc_def_id_);


--
-- TOC entry 4316 (class 1259 OID 4490463)
-- Name: act_idx_suspended_job_process_instance_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_suspended_job_process_instance_id ON basicdb_flowable8.act_ru_suspended_job USING btree (process_instance_id_);


--
-- TOC entry 4345 (class 1259 OID 4490208)
-- Name: act_idx_task_create; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_task_create ON basicdb_flowable8.act_ru_task USING btree (create_time_);


--
-- TOC entry 4346 (class 1259 OID 4490391)
-- Name: act_idx_task_exec; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_task_exec ON basicdb_flowable8.act_ru_task USING btree (execution_id_);


--
-- TOC entry 4347 (class 1259 OID 4490403)
-- Name: act_idx_task_procdef; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_task_procdef ON basicdb_flowable8.act_ru_task USING btree (proc_def_id_);


--
-- TOC entry 4348 (class 1259 OID 4490397)
-- Name: act_idx_task_procinst; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_task_procinst ON basicdb_flowable8.act_ru_task USING btree (proc_inst_id_);


--
-- TOC entry 4349 (class 1259 OID 4490209)
-- Name: act_idx_task_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_task_scope ON basicdb_flowable8.act_ru_task USING btree (scope_id_, scope_type_);


--
-- TOC entry 4350 (class 1259 OID 4490211)
-- Name: act_idx_task_scope_def; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_task_scope_def ON basicdb_flowable8.act_ru_task USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4351 (class 1259 OID 4490210)
-- Name: act_idx_task_sub_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_task_sub_scope ON basicdb_flowable8.act_ru_task USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4296 (class 1259 OID 4490102)
-- Name: act_idx_timer_job_correlation_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_timer_job_correlation_id ON basicdb_flowable8.act_ru_timer_job USING btree (correlation_id_);


--
-- TOC entry 4297 (class 1259 OID 4490101)
-- Name: act_idx_timer_job_custom_values_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_timer_job_custom_values_id ON basicdb_flowable8.act_ru_timer_job USING btree (custom_values_id_);


--
-- TOC entry 4298 (class 1259 OID 4490103)
-- Name: act_idx_timer_job_duedate; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_timer_job_duedate ON basicdb_flowable8.act_ru_timer_job USING btree (duedate_);


--
-- TOC entry 4299 (class 1259 OID 4490100)
-- Name: act_idx_timer_job_exception_stack_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_timer_job_exception_stack_id ON basicdb_flowable8.act_ru_timer_job USING btree (exception_stack_id_);


--
-- TOC entry 4300 (class 1259 OID 4490439)
-- Name: act_idx_timer_job_execution_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_timer_job_execution_id ON basicdb_flowable8.act_ru_timer_job USING btree (execution_id_);


--
-- TOC entry 4301 (class 1259 OID 4490451)
-- Name: act_idx_timer_job_proc_def_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_timer_job_proc_def_id ON basicdb_flowable8.act_ru_timer_job USING btree (proc_def_id_);


--
-- TOC entry 4302 (class 1259 OID 4490445)
-- Name: act_idx_timer_job_process_instance_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_timer_job_process_instance_id ON basicdb_flowable8.act_ru_timer_job USING btree (process_instance_id_);


--
-- TOC entry 4303 (class 1259 OID 4490166)
-- Name: act_idx_tjob_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_tjob_scope ON basicdb_flowable8.act_ru_timer_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4304 (class 1259 OID 4490168)
-- Name: act_idx_tjob_scope_def; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_tjob_scope_def ON basicdb_flowable8.act_ru_timer_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4305 (class 1259 OID 4490167)
-- Name: act_idx_tjob_sub_scope; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_tjob_sub_scope ON basicdb_flowable8.act_ru_timer_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4274 (class 1259 OID 4490373)
-- Name: act_idx_tskass_task; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_tskass_task ON basicdb_flowable8.act_ru_identitylink USING btree (task_id_);


--
-- TOC entry 4365 (class 1259 OID 4490244)
-- Name: act_idx_var_bytearray; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_var_bytearray ON basicdb_flowable8.act_ru_variable USING btree (bytearray_id_);


--
-- TOC entry 4366 (class 1259 OID 4490409)
-- Name: act_idx_var_exe; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_var_exe ON basicdb_flowable8.act_ru_variable USING btree (execution_id_);


--
-- TOC entry 4367 (class 1259 OID 4490415)
-- Name: act_idx_var_procinst; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_var_procinst ON basicdb_flowable8.act_ru_variable USING btree (proc_inst_id_);


--
-- TOC entry 4368 (class 1259 OID 4490333)
-- Name: act_idx_variable_task_id; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX act_idx_variable_task_id ON basicdb_flowable8.act_ru_variable USING btree (task_id_);


--
-- TOC entry 4218 (class 1259 OID 4489975)
-- Name: dict_type; Type: INDEX; Schema: basicdb_flowable8; Owner: postgres
--

CREATE UNIQUE INDEX dict_type ON basicdb_flowable8.sys_dict_type USING btree (dict_type);


--
-- TOC entry 4342 (class 1259 OID 4490194)
-- Name: flw_idx_batch_part; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX flw_idx_batch_part ON basicdb_flowable8.flw_ru_batch_part USING btree (batch_id_);


--
-- TOC entry 4475 (class 1259 OID 4490696)
-- Name: flw_idx_event_rsrc_dpl; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX flw_idx_event_rsrc_dpl ON basicdb_flowable8.flw_event_resource USING btree (deployment_id_);


--
-- TOC entry 4507 (class 1259 OID 4499740)
-- Name: idx_extend_procinst_model_key; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX idx_extend_procinst_model_key ON basicdb_flowable8.tbl_flow_extend_procinst USING btree (model_key);


--
-- TOC entry 4508 (class 1259 OID 4499739)
-- Name: idx_extend_procinst_proc_ins; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX idx_extend_procinst_proc_ins ON basicdb_flowable8.tbl_flow_extend_procinst USING btree (proc_ins_id);


--
-- TOC entry 4484 (class 1259 OID 4499666)
-- Name: idx_wf_comment_info_proc; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX idx_wf_comment_info_proc ON basicdb_flowable8.wf_comment_info USING btree (proc_ins_id);


--
-- TOC entry 4487 (class 1259 OID 4499667)
-- Name: idx_wf_copy_instance; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX idx_wf_copy_instance ON basicdb_flowable8.wf_copy USING btree (instance_id);


--
-- TOC entry 4490 (class 1259 OID 4499665)
-- Name: idx_wf_deploy_form_act; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX idx_wf_deploy_form_act ON basicdb_flowable8.wf_deploy_form USING btree (deploy_id, activity_id);


--
-- TOC entry 4491 (class 1259 OID 4499663)
-- Name: idx_wf_deploy_form_deploy; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX idx_wf_deploy_form_deploy ON basicdb_flowable8.wf_deploy_form USING btree (deploy_id);


--
-- TOC entry 4492 (class 1259 OID 4499664)
-- Name: idx_wf_deploy_form_form; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX idx_wf_deploy_form_form ON basicdb_flowable8.wf_deploy_form USING btree (form_id);


--
-- TOC entry 4502 (class 1259 OID 4499662)
-- Name: idx_wf_task_form_act; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX idx_wf_task_form_act ON basicdb_flowable8.wf_task_form USING btree (activity_id);


--
-- TOC entry 4503 (class 1259 OID 4499660)
-- Name: idx_wf_task_form_proc_ins; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX idx_wf_task_form_proc_ins ON basicdb_flowable8.wf_task_form USING btree (proc_ins_id);


--
-- TOC entry 4504 (class 1259 OID 4499661)
-- Name: idx_wf_task_form_task; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE INDEX idx_wf_task_form_task ON basicdb_flowable8.wf_task_form USING btree (task_id);


--
-- TOC entry 4207 (class 1259 OID 4489976)
-- Name: sched_name; Type: INDEX; Schema: basicdb_flowable8; Owner: postgres
--

CREATE INDEX sched_name ON basicdb_flowable8.qrtz_triggers USING btree (sched_name, job_name, job_group);


--
-- TOC entry 4233 (class 1259 OID 4489977)
-- Name: uk_sys_notice_read_notice_user; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE UNIQUE INDEX uk_sys_notice_read_notice_user ON basicdb_flowable8.sys_notice_read USING btree (notice_id, user_id);


--
-- TOC entry 4497 (class 1259 OID 4499659)
-- Name: uk_wf_form_code; Type: INDEX; Schema: basicdb_flowable8; Owner: system
--

CREATE UNIQUE INDEX uk_wf_form_code ON basicdb_flowable8.wf_form USING btree (code) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 4514 (class 2606 OID 4490380)
-- Name: act_ru_identitylink act_fk_athrz_procedef; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_identitylink
    ADD CONSTRAINT act_fk_athrz_procedef FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable8.act_re_procdef(id_);


--
-- TOC entry 4513 (class 2606 OID 4490342)
-- Name: act_ge_bytearray act_fk_bytearr_depl; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ge_bytearray
    ADD CONSTRAINT act_fk_bytearr_depl FOREIGN KEY (deployment_id_) REFERENCES basicdb_flowable8.act_re_deployment(id_);


--
-- TOC entry 4532 (class 2606 OID 4490148)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4533 (class 2606 OID 4490143)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4534 (class 2606 OID 4490476)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_execution; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_execution FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4535 (class 2606 OID 4490488)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_proc_def; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable8.act_re_procdef(id_);


--
-- TOC entry 4536 (class 2606 OID 4490482)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_process_instance; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4546 (class 2606 OID 4490493)
-- Name: act_ru_event_subscr act_fk_event_exec; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_event_subscr
    ADD CONSTRAINT act_fk_event_exec FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4550 (class 2606 OID 4490356)
-- Name: act_ru_execution act_fk_exe_parent; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_execution
    ADD CONSTRAINT act_fk_exe_parent FOREIGN KEY (parent_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4551 (class 2606 OID 4490368)
-- Name: act_ru_execution act_fk_exe_procdef; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procdef FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable8.act_re_procdef(id_);


--
-- TOC entry 4552 (class 2606 OID 4490350)
-- Name: act_ru_execution act_fk_exe_procinst; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procinst FOREIGN KEY (proc_inst_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4553 (class 2606 OID 4490362)
-- Name: act_ru_execution act_fk_exe_super; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_execution
    ADD CONSTRAINT act_fk_exe_super FOREIGN KEY (super_exec_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4537 (class 2606 OID 4490158)
-- Name: act_ru_external_job act_fk_external_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4538 (class 2606 OID 4490153)
-- Name: act_ru_external_job act_fk_external_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4515 (class 2606 OID 4490386)
-- Name: act_ru_identitylink act_fk_idl_procinst; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_identitylink
    ADD CONSTRAINT act_fk_idl_procinst FOREIGN KEY (proc_inst_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4554 (class 2606 OID 4490517)
-- Name: act_procdef_info act_fk_info_json_ba; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_procdef_info
    ADD CONSTRAINT act_fk_info_json_ba FOREIGN KEY (info_json_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4555 (class 2606 OID 4490523)
-- Name: act_procdef_info act_fk_info_procdef; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_procdef_info
    ADD CONSTRAINT act_fk_info_procdef FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable8.act_re_procdef(id_);


--
-- TOC entry 4517 (class 2606 OID 4490118)
-- Name: act_ru_job act_fk_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_job
    ADD CONSTRAINT act_fk_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4518 (class 2606 OID 4490113)
-- Name: act_ru_job act_fk_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_job
    ADD CONSTRAINT act_fk_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4519 (class 2606 OID 4490422)
-- Name: act_ru_job act_fk_job_execution; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_job
    ADD CONSTRAINT act_fk_job_execution FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4520 (class 2606 OID 4490434)
-- Name: act_ru_job act_fk_job_proc_def; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_job
    ADD CONSTRAINT act_fk_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable8.act_re_procdef(id_);


--
-- TOC entry 4521 (class 2606 OID 4490428)
-- Name: act_ru_job act_fk_job_process_instance; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_job
    ADD CONSTRAINT act_fk_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4556 (class 2606 OID 4490649)
-- Name: act_id_membership act_fk_memb_group; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_membership
    ADD CONSTRAINT act_fk_memb_group FOREIGN KEY (group_id_) REFERENCES basicdb_flowable8.act_id_group(id_);


--
-- TOC entry 4557 (class 2606 OID 4490655)
-- Name: act_id_membership act_fk_memb_user; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_membership
    ADD CONSTRAINT act_fk_memb_user FOREIGN KEY (user_id_) REFERENCES basicdb_flowable8.act_id_user(id_);


--
-- TOC entry 4547 (class 2606 OID 4490511)
-- Name: act_re_model act_fk_model_deployment; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_re_model
    ADD CONSTRAINT act_fk_model_deployment FOREIGN KEY (deployment_id_) REFERENCES basicdb_flowable8.act_re_deployment(id_);


--
-- TOC entry 4548 (class 2606 OID 4490499)
-- Name: act_re_model act_fk_model_source; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_re_model
    ADD CONSTRAINT act_fk_model_source FOREIGN KEY (editor_source_value_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4549 (class 2606 OID 4490505)
-- Name: act_re_model act_fk_model_source_extra; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_re_model
    ADD CONSTRAINT act_fk_model_source_extra FOREIGN KEY (editor_source_extra_value_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4558 (class 2606 OID 4490661)
-- Name: act_id_priv_mapping act_fk_priv_mapping; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_id_priv_mapping
    ADD CONSTRAINT act_fk_priv_mapping FOREIGN KEY (priv_id_) REFERENCES basicdb_flowable8.act_id_priv(id_);


--
-- TOC entry 4527 (class 2606 OID 4490138)
-- Name: act_ru_suspended_job act_fk_suspended_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4528 (class 2606 OID 4490133)
-- Name: act_ru_suspended_job act_fk_suspended_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4529 (class 2606 OID 4490458)
-- Name: act_ru_suspended_job act_fk_suspended_job_execution; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_execution FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4530 (class 2606 OID 4490470)
-- Name: act_ru_suspended_job act_fk_suspended_job_proc_def; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable8.act_re_procdef(id_);


--
-- TOC entry 4531 (class 2606 OID 4490464)
-- Name: act_ru_suspended_job act_fk_suspended_job_process_instance; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4540 (class 2606 OID 4490392)
-- Name: act_ru_task act_fk_task_exe; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_task
    ADD CONSTRAINT act_fk_task_exe FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4541 (class 2606 OID 4490404)
-- Name: act_ru_task act_fk_task_procdef; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_task
    ADD CONSTRAINT act_fk_task_procdef FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable8.act_re_procdef(id_);


--
-- TOC entry 4542 (class 2606 OID 4490398)
-- Name: act_ru_task act_fk_task_procinst; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_task
    ADD CONSTRAINT act_fk_task_procinst FOREIGN KEY (proc_inst_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4522 (class 2606 OID 4490128)
-- Name: act_ru_timer_job act_fk_timer_job_custom_values; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4523 (class 2606 OID 4490123)
-- Name: act_ru_timer_job act_fk_timer_job_exception; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4524 (class 2606 OID 4490440)
-- Name: act_ru_timer_job act_fk_timer_job_execution; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_execution FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4525 (class 2606 OID 4490452)
-- Name: act_ru_timer_job act_fk_timer_job_proc_def; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES basicdb_flowable8.act_re_procdef(id_);


--
-- TOC entry 4526 (class 2606 OID 4490446)
-- Name: act_ru_timer_job act_fk_timer_job_process_instance; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4516 (class 2606 OID 4490374)
-- Name: act_ru_identitylink act_fk_tskass_task; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_identitylink
    ADD CONSTRAINT act_fk_tskass_task FOREIGN KEY (task_id_) REFERENCES basicdb_flowable8.act_ru_task(id_);


--
-- TOC entry 4543 (class 2606 OID 4490245)
-- Name: act_ru_variable act_fk_var_bytearray; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_variable
    ADD CONSTRAINT act_fk_var_bytearray FOREIGN KEY (bytearray_id_) REFERENCES basicdb_flowable8.act_ge_bytearray(id_);


--
-- TOC entry 4544 (class 2606 OID 4490410)
-- Name: act_ru_variable act_fk_var_exe; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_variable
    ADD CONSTRAINT act_fk_var_exe FOREIGN KEY (execution_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4545 (class 2606 OID 4490416)
-- Name: act_ru_variable act_fk_var_procinst; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.act_ru_variable
    ADD CONSTRAINT act_fk_var_procinst FOREIGN KEY (proc_inst_id_) REFERENCES basicdb_flowable8.act_ru_execution(id_);


--
-- TOC entry 4539 (class 2606 OID 4490195)
-- Name: flw_ru_batch_part flw_fk_batch_part_parent; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.flw_ru_batch_part
    ADD CONSTRAINT flw_fk_batch_part_parent FOREIGN KEY (batch_id_) REFERENCES basicdb_flowable8.flw_ru_batch(id_);


--
-- TOC entry 4559 (class 2606 OID 4490691)
-- Name: flw_event_resource flw_fk_event_rsrc_dpl; Type: FK CONSTRAINT; Schema: basicdb_flowable8; Owner: system
--

ALTER TABLE ONLY basicdb_flowable8.flw_event_resource
    ADD CONSTRAINT flw_fk_event_rsrc_dpl FOREIGN KEY (deployment_id_) REFERENCES basicdb_flowable8.flw_event_deployment(id_);


--
-- TOC entry 4823 (class 0 OID 0)
-- Dependencies: 377
-- Name: TABLE gen_table; Type: ACL; Schema: basicdb_flowable8; Owner: system
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.gen_table TO PUBLIC;


--
-- TOC entry 4824 (class 0 OID 0)
-- Dependencies: 378
-- Name: TABLE gen_table_column; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.gen_table_column TO PUBLIC;


--
-- TOC entry 4826 (class 0 OID 0)
-- Dependencies: 379
-- Name: SEQUENCE gen_table_column_column_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.gen_table_column_column_id_seq TO PUBLIC;


--
-- TOC entry 4828 (class 0 OID 0)
-- Dependencies: 380
-- Name: SEQUENCE gen_table_table_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: system
--

GRANT ALL ON SEQUENCE basicdb_flowable8.gen_table_table_id_seq TO PUBLIC;


--
-- TOC entry 4829 (class 0 OID 0)
-- Dependencies: 381
-- Name: TABLE list_column; Type: ACL; Schema: basicdb_flowable8; Owner: system
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.list_column TO PUBLIC;


--
-- TOC entry 4830 (class 0 OID 0)
-- Dependencies: 382
-- Name: TABLE list_table; Type: ACL; Schema: basicdb_flowable8; Owner: system
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.list_table TO PUBLIC;


--
-- TOC entry 4831 (class 0 OID 0)
-- Dependencies: 383
-- Name: TABLE qrtz_blob_triggers; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.qrtz_blob_triggers TO PUBLIC;


--
-- TOC entry 4832 (class 0 OID 0)
-- Dependencies: 384
-- Name: TABLE qrtz_calendars; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.qrtz_calendars TO PUBLIC;


--
-- TOC entry 4833 (class 0 OID 0)
-- Dependencies: 385
-- Name: TABLE qrtz_cron_triggers; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.qrtz_cron_triggers TO PUBLIC;


--
-- TOC entry 4834 (class 0 OID 0)
-- Dependencies: 386
-- Name: TABLE qrtz_fired_triggers; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.qrtz_fired_triggers TO PUBLIC;


--
-- TOC entry 4835 (class 0 OID 0)
-- Dependencies: 387
-- Name: TABLE qrtz_job_details; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.qrtz_job_details TO PUBLIC;


--
-- TOC entry 4836 (class 0 OID 0)
-- Dependencies: 388
-- Name: TABLE qrtz_locks; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.qrtz_locks TO PUBLIC;


--
-- TOC entry 4837 (class 0 OID 0)
-- Dependencies: 389
-- Name: TABLE qrtz_paused_trigger_grps; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.qrtz_paused_trigger_grps TO PUBLIC;


--
-- TOC entry 4838 (class 0 OID 0)
-- Dependencies: 390
-- Name: TABLE qrtz_scheduler_state; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.qrtz_scheduler_state TO PUBLIC;


--
-- TOC entry 4839 (class 0 OID 0)
-- Dependencies: 391
-- Name: TABLE qrtz_simple_triggers; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.qrtz_simple_triggers TO PUBLIC;


--
-- TOC entry 4840 (class 0 OID 0)
-- Dependencies: 392
-- Name: TABLE qrtz_simprop_triggers; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.qrtz_simprop_triggers TO PUBLIC;


--
-- TOC entry 4841 (class 0 OID 0)
-- Dependencies: 393
-- Name: TABLE qrtz_triggers; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.qrtz_triggers TO PUBLIC;


--
-- TOC entry 4842 (class 0 OID 0)
-- Dependencies: 394
-- Name: TABLE sys_config; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_config TO PUBLIC;


--
-- TOC entry 4844 (class 0 OID 0)
-- Dependencies: 395
-- Name: SEQUENCE sys_config_config_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_config_config_id_seq TO PUBLIC;


--
-- TOC entry 4845 (class 0 OID 0)
-- Dependencies: 397
-- Name: TABLE sys_dept; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_dept TO PUBLIC;


--
-- TOC entry 4847 (class 0 OID 0)
-- Dependencies: 398
-- Name: SEQUENCE sys_dept_dept_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_dept_dept_id_seq TO PUBLIC;


--
-- TOC entry 4848 (class 0 OID 0)
-- Dependencies: 399
-- Name: TABLE sys_dict_data; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_dict_data TO PUBLIC;


--
-- TOC entry 4850 (class 0 OID 0)
-- Dependencies: 400
-- Name: SEQUENCE sys_dict_data_dict_code_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_dict_data_dict_code_seq TO PUBLIC;


--
-- TOC entry 4851 (class 0 OID 0)
-- Dependencies: 401
-- Name: TABLE sys_dict_type; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_dict_type TO PUBLIC;


--
-- TOC entry 4853 (class 0 OID 0)
-- Dependencies: 402
-- Name: SEQUENCE sys_dict_type_dict_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_dict_type_dict_id_seq TO PUBLIC;


--
-- TOC entry 4857 (class 0 OID 0)
-- Dependencies: 405
-- Name: TABLE sys_job; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_job TO PUBLIC;


--
-- TOC entry 4859 (class 0 OID 0)
-- Dependencies: 406
-- Name: SEQUENCE sys_job_job_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_job_job_id_seq TO PUBLIC;


--
-- TOC entry 4860 (class 0 OID 0)
-- Dependencies: 407
-- Name: TABLE sys_job_log; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_job_log TO PUBLIC;


--
-- TOC entry 4862 (class 0 OID 0)
-- Dependencies: 408
-- Name: SEQUENCE sys_job_log_job_log_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_job_log_job_log_id_seq TO PUBLIC;


--
-- TOC entry 4863 (class 0 OID 0)
-- Dependencies: 409
-- Name: TABLE sys_logininfor; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_logininfor TO PUBLIC;


--
-- TOC entry 4865 (class 0 OID 0)
-- Dependencies: 410
-- Name: SEQUENCE sys_logininfor_info_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_logininfor_info_id_seq TO PUBLIC;


--
-- TOC entry 4867 (class 0 OID 0)
-- Dependencies: 411
-- Name: TABLE sys_menu; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_menu TO PUBLIC;


--
-- TOC entry 4869 (class 0 OID 0)
-- Dependencies: 412
-- Name: SEQUENCE sys_menu_menu_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_menu_menu_id_seq TO PUBLIC;


--
-- TOC entry 4870 (class 0 OID 0)
-- Dependencies: 413
-- Name: TABLE sys_notice; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_notice TO PUBLIC;


--
-- TOC entry 4872 (class 0 OID 0)
-- Dependencies: 414
-- Name: SEQUENCE sys_notice_notice_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_notice_notice_id_seq TO PUBLIC;


--
-- TOC entry 4874 (class 0 OID 0)
-- Dependencies: 417
-- Name: TABLE sys_oper_log; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_oper_log TO PUBLIC;


--
-- TOC entry 4876 (class 0 OID 0)
-- Dependencies: 418
-- Name: SEQUENCE sys_oper_log_oper_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_oper_log_oper_id_seq TO PUBLIC;


--
-- TOC entry 4877 (class 0 OID 0)
-- Dependencies: 419
-- Name: TABLE sys_post; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_post TO PUBLIC;


--
-- TOC entry 4879 (class 0 OID 0)
-- Dependencies: 420
-- Name: SEQUENCE sys_post_post_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_post_post_id_seq TO PUBLIC;


--
-- TOC entry 4880 (class 0 OID 0)
-- Dependencies: 421
-- Name: TABLE sys_role; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_role TO PUBLIC;


--
-- TOC entry 4881 (class 0 OID 0)
-- Dependencies: 422
-- Name: TABLE sys_role_dept; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_role_dept TO PUBLIC;


--
-- TOC entry 4882 (class 0 OID 0)
-- Dependencies: 423
-- Name: TABLE sys_role_menu; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_role_menu TO PUBLIC;


--
-- TOC entry 4884 (class 0 OID 0)
-- Dependencies: 424
-- Name: SEQUENCE sys_role_role_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_role_role_id_seq TO PUBLIC;


--
-- TOC entry 4885 (class 0 OID 0)
-- Dependencies: 425
-- Name: TABLE sys_user; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_user TO PUBLIC;


--
-- TOC entry 4886 (class 0 OID 0)
-- Dependencies: 426
-- Name: TABLE sys_user_post; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_user_post TO PUBLIC;


--
-- TOC entry 4887 (class 0 OID 0)
-- Dependencies: 427
-- Name: TABLE sys_user_role; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_flowable8.sys_user_role TO PUBLIC;


--
-- TOC entry 4889 (class 0 OID 0)
-- Dependencies: 428
-- Name: SEQUENCE sys_user_user_id_seq; Type: ACL; Schema: basicdb_flowable8; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_flowable8.sys_user_user_id_seq TO PUBLIC;


-- Completed on 2026-10-17 21:26:28

--
-- PostgreSQL database dump complete
--

