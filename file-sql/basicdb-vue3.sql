--
-- PostgreSQL database dump
--

-- Dumped from database version 15.1
-- Dumped by pg_dump version 17.0

-- Started on 2026-10-16 17:08:19

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

DROP INDEX basicdb_agentscope.uk_sys_notice_read_notice_user;
DROP INDEX basicdb_agentscope.sched_name;
DROP INDEX basicdb_agentscope.dict_type;
ALTER TABLE ONLY basicdb_agentscope.sys_notice_read DROP CONSTRAINT sys_notice_read_pkey;
ALTER TABLE ONLY basicdb_agentscope.sys_files DROP CONSTRAINT sys_files_pkey;
ALTER TABLE ONLY basicdb_agentscope.sys_demo DROP CONSTRAINT sys_demo_pkey;
ALTER TABLE ONLY basicdb_agentscope.sys_user_role DROP CONSTRAINT "PRIMARY00028";
ALTER TABLE ONLY basicdb_agentscope.sys_user_post DROP CONSTRAINT "PRIMARY00027";
ALTER TABLE ONLY basicdb_agentscope.sys_user DROP CONSTRAINT "PRIMARY00026";
ALTER TABLE ONLY basicdb_agentscope.sys_role_menu DROP CONSTRAINT "PRIMARY00025";
ALTER TABLE ONLY basicdb_agentscope.sys_role_dept DROP CONSTRAINT "PRIMARY00024";
ALTER TABLE ONLY basicdb_agentscope.sys_role DROP CONSTRAINT "PRIMARY00023";
ALTER TABLE ONLY basicdb_agentscope.sys_post DROP CONSTRAINT "PRIMARY00022";
ALTER TABLE ONLY basicdb_agentscope.sys_oper_log DROP CONSTRAINT "PRIMARY00021";
ALTER TABLE ONLY basicdb_agentscope.sys_notice DROP CONSTRAINT "PRIMARY00020";
ALTER TABLE ONLY basicdb_agentscope.sys_menu DROP CONSTRAINT "PRIMARY00019";
ALTER TABLE ONLY basicdb_agentscope.sys_logininfor DROP CONSTRAINT "PRIMARY00018";
ALTER TABLE ONLY basicdb_agentscope.sys_job_log DROP CONSTRAINT "PRIMARY00017";
ALTER TABLE ONLY basicdb_agentscope.sys_job DROP CONSTRAINT "PRIMARY00016";
ALTER TABLE ONLY basicdb_agentscope.sys_dict_type DROP CONSTRAINT "PRIMARY00015";
ALTER TABLE ONLY basicdb_agentscope.sys_dict_data DROP CONSTRAINT "PRIMARY00014";
ALTER TABLE ONLY basicdb_agentscope.sys_dept DROP CONSTRAINT "PRIMARY00013";
ALTER TABLE ONLY basicdb_agentscope.sys_config DROP CONSTRAINT "PRIMARY00012";
ALTER TABLE ONLY basicdb_agentscope.qrtz_triggers DROP CONSTRAINT "PRIMARY00011";
ALTER TABLE ONLY basicdb_agentscope.qrtz_simprop_triggers DROP CONSTRAINT "PRIMARY00010";
ALTER TABLE ONLY basicdb_agentscope.qrtz_simple_triggers DROP CONSTRAINT "PRIMARY00009";
ALTER TABLE ONLY basicdb_agentscope.qrtz_scheduler_state DROP CONSTRAINT "PRIMARY00008";
ALTER TABLE ONLY basicdb_agentscope.qrtz_paused_trigger_grps DROP CONSTRAINT "PRIMARY00007";
ALTER TABLE ONLY basicdb_agentscope.qrtz_locks DROP CONSTRAINT "PRIMARY00006";
ALTER TABLE ONLY basicdb_agentscope.qrtz_job_details DROP CONSTRAINT "PRIMARY00005";
ALTER TABLE ONLY basicdb_agentscope.qrtz_fired_triggers DROP CONSTRAINT "PRIMARY00004";
ALTER TABLE ONLY basicdb_agentscope.qrtz_cron_triggers DROP CONSTRAINT "PRIMARY00003";
ALTER TABLE ONLY basicdb_agentscope.qrtz_calendars DROP CONSTRAINT "PRIMARY00002";
ALTER TABLE ONLY basicdb_agentscope.qrtz_blob_triggers DROP CONSTRAINT "PRIMARY00001";
ALTER TABLE ONLY basicdb_agentscope.gen_table_column DROP CONSTRAINT "PRIMARY00000";
ALTER TABLE ONLY basicdb_agentscope.gen_table DROP CONSTRAINT "PRIMARY";
ALTER TABLE basicdb_agentscope.sys_user ALTER COLUMN user_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.sys_role ALTER COLUMN role_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.sys_post ALTER COLUMN post_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.sys_oper_log ALTER COLUMN oper_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.sys_notice ALTER COLUMN notice_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.sys_menu ALTER COLUMN menu_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.sys_logininfor ALTER COLUMN info_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.sys_job_log ALTER COLUMN job_log_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.sys_job ALTER COLUMN job_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.sys_dict_type ALTER COLUMN dict_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.sys_dict_data ALTER COLUMN dict_code DROP DEFAULT;
ALTER TABLE basicdb_agentscope.sys_dept ALTER COLUMN dept_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.sys_config ALTER COLUMN config_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.gen_table_column ALTER COLUMN column_id DROP DEFAULT;
ALTER TABLE basicdb_agentscope.gen_table ALTER COLUMN table_id DROP DEFAULT;
DROP SEQUENCE basicdb_agentscope.sys_user_user_id_seq;
DROP TABLE basicdb_agentscope.sys_user_role;
DROP TABLE basicdb_agentscope.sys_user_post;
DROP TABLE basicdb_agentscope.sys_user;
DROP SEQUENCE basicdb_agentscope.sys_role_role_id_seq;
DROP TABLE basicdb_agentscope.sys_role_menu;
DROP TABLE basicdb_agentscope.sys_role_dept;
DROP TABLE basicdb_agentscope.sys_role;
DROP SEQUENCE basicdb_agentscope.sys_post_post_id_seq;
DROP TABLE basicdb_agentscope.sys_post;
DROP SEQUENCE basicdb_agentscope.sys_oper_log_oper_id_seq;
DROP TABLE basicdb_agentscope.sys_oper_log;
DROP SEQUENCE basicdb_agentscope.sys_notice_read_read_id_seq;
DROP TABLE basicdb_agentscope.sys_notice_read;
DROP SEQUENCE basicdb_agentscope.sys_notice_notice_id_seq;
DROP TABLE basicdb_agentscope.sys_notice;
DROP SEQUENCE basicdb_agentscope.sys_menu_menu_id_seq;
DROP TABLE basicdb_agentscope.sys_menu;
DROP SEQUENCE basicdb_agentscope.sys_logininfor_info_id_seq;
DROP TABLE basicdb_agentscope.sys_logininfor;
DROP SEQUENCE basicdb_agentscope.sys_job_log_job_log_id_seq;
DROP TABLE basicdb_agentscope.sys_job_log;
DROP SEQUENCE basicdb_agentscope.sys_job_job_id_seq;
DROP TABLE basicdb_agentscope.sys_job;
DROP TABLE basicdb_agentscope.sys_i18n;
DROP TABLE basicdb_agentscope.sys_files;
DROP SEQUENCE basicdb_agentscope.sys_dict_type_dict_id_seq;
DROP TABLE basicdb_agentscope.sys_dict_type;
DROP SEQUENCE basicdb_agentscope.sys_dict_data_dict_code_seq;
DROP TABLE basicdb_agentscope.sys_dict_data;
DROP SEQUENCE basicdb_agentscope.sys_dept_dept_id_seq;
DROP TABLE basicdb_agentscope.sys_dept;
DROP TABLE basicdb_agentscope.sys_demo;
DROP SEQUENCE basicdb_agentscope.sys_config_config_id_seq;
DROP TABLE basicdb_agentscope.sys_config;
DROP TABLE basicdb_agentscope.qrtz_triggers;
DROP TABLE basicdb_agentscope.qrtz_simprop_triggers;
DROP TABLE basicdb_agentscope.qrtz_simple_triggers;
DROP TABLE basicdb_agentscope.qrtz_scheduler_state;
DROP TABLE basicdb_agentscope.qrtz_paused_trigger_grps;
DROP TABLE basicdb_agentscope.qrtz_locks;
DROP TABLE basicdb_agentscope.qrtz_job_details;
DROP TABLE basicdb_agentscope.qrtz_fired_triggers;
DROP TABLE basicdb_agentscope.qrtz_cron_triggers;
DROP TABLE basicdb_agentscope.qrtz_calendars;
DROP TABLE basicdb_agentscope.qrtz_blob_triggers;
DROP VIEW basicdb_agentscope.list_table;
DROP VIEW basicdb_agentscope.list_column;
DROP SEQUENCE basicdb_agentscope.gen_table_table_id_seq;
DROP SEQUENCE basicdb_agentscope.gen_table_column_column_id_seq;
DROP TABLE basicdb_agentscope.gen_table_column;
DROP TABLE basicdb_agentscope.gen_table;
DROP FUNCTION basicdb_agentscope.tabledef(oid);
DROP FUNCTION basicdb_agentscope.ifnull(param1 text, param2 text);
DROP FUNCTION basicdb_agentscope.find_in_set(bigint, character varying);
DROP FUNCTION basicdb_agentscope.exec(sqlstring character varying);
DROP FUNCTION basicdb_agentscope.date_format(indate anyelement, informate text);
DROP SCHEMA basicdb_agentscope;
--
-- TOC entry 8 (class 2615 OID 4483157)
-- Name: basicdb_agentscope; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA basicdb_agentscope;


ALTER SCHEMA basicdb_agentscope OWNER TO postgres;

--
-- TOC entry 387 (class 1255 OID 4483158)
-- Name: date_format(anyelement, text); Type: FUNCTION; Schema: basicdb_agentscope; Owner: system
--

CREATE FUNCTION basicdb_agentscope.date_format(indate anyelement, informate text) RETURNS text
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


ALTER FUNCTION basicdb_agentscope.date_format(indate anyelement, informate text) OWNER TO system;

--
-- TOC entry 388 (class 1255 OID 4483159)
-- Name: exec(character varying); Type: FUNCTION; Schema: basicdb_agentscope; Owner: system
--

CREATE FUNCTION basicdb_agentscope.exec(sqlstring character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
declare
res varchar(50);
BEGIN
EXECUTE sqlstring;
RETURN 'ok';
END
$$;


ALTER FUNCTION basicdb_agentscope.exec(sqlstring character varying) OWNER TO system;

--
-- TOC entry 389 (class 1255 OID 4483160)
-- Name: find_in_set(bigint, character varying); Type: FUNCTION; Schema: basicdb_agentscope; Owner: system
--

CREATE FUNCTION basicdb_agentscope.find_in_set(bigint, character varying) RETURNS boolean
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


ALTER FUNCTION basicdb_agentscope.find_in_set(bigint, character varying) OWNER TO system;

--
-- TOC entry 390 (class 1255 OID 4483161)
-- Name: ifnull(text, text); Type: FUNCTION; Schema: basicdb_agentscope; Owner: system
--

CREATE FUNCTION basicdb_agentscope.ifnull(param1 text, param2 text) RETURNS text
    LANGUAGE plpgsql
    AS $_$
BEGIN
    return COALESCE($1,$2);
END;
$_$;


ALTER FUNCTION basicdb_agentscope.ifnull(param1 text, param2 text) OWNER TO system;

--
-- TOC entry 391 (class 1255 OID 4483162)
-- Name: tabledef(oid); Type: FUNCTION; Schema: basicdb_agentscope; Owner: system
--

CREATE FUNCTION basicdb_agentscope.tabledef(oid) RETURNS text
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


ALTER FUNCTION basicdb_agentscope.tabledef(oid) OWNER TO system;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 218 (class 1259 OID 4483163)
-- Name: gen_table; Type: TABLE; Schema: basicdb_agentscope; Owner: system
--

CREATE TABLE basicdb_agentscope.gen_table (
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


ALTER TABLE basicdb_agentscope.gen_table OWNER TO system;

--
-- TOC entry 219 (class 1259 OID 4483171)
-- Name: gen_table_column; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.gen_table_column (
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


ALTER TABLE basicdb_agentscope.gen_table_column OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 4483177)
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.gen_table_column_column_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.gen_table_column_column_id_seq OWNER TO postgres;

--
-- TOC entry 3972 (class 0 OID 0)
-- Dependencies: 220
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.gen_table_column_column_id_seq OWNED BY basicdb_agentscope.gen_table_column.column_id;


--
-- TOC entry 221 (class 1259 OID 4483178)
-- Name: gen_table_table_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: system
--

CREATE SEQUENCE basicdb_agentscope.gen_table_table_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.gen_table_table_id_seq OWNER TO system;

--
-- TOC entry 3974 (class 0 OID 0)
-- Dependencies: 221
-- Name: gen_table_table_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: system
--

ALTER SEQUENCE basicdb_agentscope.gen_table_table_id_seq OWNED BY basicdb_agentscope.gen_table.table_id;


--
-- TOC entry 222 (class 1259 OID 4483179)
-- Name: list_column; Type: VIEW; Schema: basicdb_agentscope; Owner: system
--

CREATE VIEW basicdb_agentscope.list_column AS
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
  WHERE ((c.relkind = ANY (ARRAY['r'::"char", 'p'::"char"])) AND (a.attnum > 0) AND (n.nspname = 'basicdb_agentscope'::name))
  ORDER BY c.relname, a.attnum;


ALTER VIEW basicdb_agentscope.list_column OWNER TO system;

--
-- TOC entry 223 (class 1259 OID 4483184)
-- Name: list_table; Type: VIEW; Schema: basicdb_agentscope; Owner: system
--

CREATE VIEW basicdb_agentscope.list_table AS
 SELECT c.relname AS table_name,
    obj_description(c.oid) AS table_comment,
    CURRENT_TIMESTAMP AS create_time,
    CURRENT_TIMESTAMP AS update_time
   FROM (pg_class c
     LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace)))
  WHERE ((c.relkind = ANY (ARRAY['r'::"char", 'p'::"char"])) AND (c.relname !~~ 'spatial_%'::text) AND (n.nspname = 'basicdb_agentscope'::name) AND (n.nspname <> ''::name));


ALTER VIEW basicdb_agentscope.list_table OWNER TO system;

--
-- TOC entry 224 (class 1259 OID 4483189)
-- Name: qrtz_blob_triggers; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.qrtz_blob_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea
);


ALTER TABLE basicdb_agentscope.qrtz_blob_triggers OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 4483194)
-- Name: qrtz_calendars; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.qrtz_calendars (
    sched_name character varying(120) NOT NULL,
    calendar_name character varying(200) NOT NULL,
    calendar bytea NOT NULL
);


ALTER TABLE basicdb_agentscope.qrtz_calendars OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 4483199)
-- Name: qrtz_cron_triggers; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.qrtz_cron_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(200) NOT NULL,
    time_zone_id character varying(80)
);


ALTER TABLE basicdb_agentscope.qrtz_cron_triggers OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 4483204)
-- Name: qrtz_fired_triggers; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.qrtz_fired_triggers (
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


ALTER TABLE basicdb_agentscope.qrtz_fired_triggers OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 4483209)
-- Name: qrtz_job_details; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.qrtz_job_details (
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


ALTER TABLE basicdb_agentscope.qrtz_job_details OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 4483214)
-- Name: qrtz_locks; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.qrtz_locks (
    sched_name character varying(120) NOT NULL,
    lock_name character varying(40) NOT NULL
);


ALTER TABLE basicdb_agentscope.qrtz_locks OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 4483217)
-- Name: qrtz_paused_trigger_grps; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.qrtz_paused_trigger_grps (
    sched_name character varying(120) NOT NULL,
    trigger_group character varying(200) NOT NULL
);


ALTER TABLE basicdb_agentscope.qrtz_paused_trigger_grps OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 4483220)
-- Name: qrtz_scheduler_state; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.qrtz_scheduler_state (
    sched_name character varying(120) NOT NULL,
    instance_name character varying(200) NOT NULL,
    last_checkin_time bigint NOT NULL,
    checkin_interval bigint NOT NULL
);


ALTER TABLE basicdb_agentscope.qrtz_scheduler_state OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 4483223)
-- Name: qrtz_simple_triggers; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.qrtz_simple_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count bigint NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL
);


ALTER TABLE basicdb_agentscope.qrtz_simple_triggers OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 4483228)
-- Name: qrtz_simprop_triggers; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.qrtz_simprop_triggers (
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


ALTER TABLE basicdb_agentscope.qrtz_simprop_triggers OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 4483233)
-- Name: qrtz_triggers; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.qrtz_triggers (
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


ALTER TABLE basicdb_agentscope.qrtz_triggers OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 4483238)
-- Name: sys_config; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_config (
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


ALTER TABLE basicdb_agentscope.sys_config OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 4483244)
-- Name: sys_config_config_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_config_config_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_config_config_id_seq OWNER TO postgres;

--
-- TOC entry 3990 (class 0 OID 0)
-- Dependencies: 236
-- Name: sys_config_config_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_config_config_id_seq OWNED BY basicdb_agentscope.sys_config.config_id;


--
-- TOC entry 237 (class 1259 OID 4483245)
-- Name: sys_demo; Type: TABLE; Schema: basicdb_agentscope; Owner: system
--

CREATE TABLE basicdb_agentscope.sys_demo (
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


ALTER TABLE basicdb_agentscope.sys_demo OWNER TO system;

--
-- TOC entry 238 (class 1259 OID 4483248)
-- Name: sys_dept; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_dept (
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


ALTER TABLE basicdb_agentscope.sys_dept OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 4483257)
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_dept_dept_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_dept_dept_id_seq OWNER TO postgres;

--
-- TOC entry 3993 (class 0 OID 0)
-- Dependencies: 239
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_dept_dept_id_seq OWNED BY basicdb_agentscope.sys_dept.dept_id;


--
-- TOC entry 240 (class 1259 OID 4483258)
-- Name: sys_dict_data; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_dict_data (
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


ALTER TABLE basicdb_agentscope.sys_dict_data OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 4483266)
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_dict_data_dict_code_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_dict_data_dict_code_seq OWNER TO postgres;

--
-- TOC entry 3996 (class 0 OID 0)
-- Dependencies: 241
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_dict_data_dict_code_seq OWNED BY basicdb_agentscope.sys_dict_data.dict_code;


--
-- TOC entry 242 (class 1259 OID 4483267)
-- Name: sys_dict_type; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_dict_type (
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


ALTER TABLE basicdb_agentscope.sys_dict_type OWNER TO postgres;

--
-- TOC entry 243 (class 1259 OID 4483273)
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_dict_type_dict_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_dict_type_dict_id_seq OWNER TO postgres;

--
-- TOC entry 3999 (class 0 OID 0)
-- Dependencies: 243
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_dict_type_dict_id_seq OWNED BY basicdb_agentscope.sys_dict_type.dict_id;


--
-- TOC entry 244 (class 1259 OID 4483274)
-- Name: sys_files; Type: TABLE; Schema: basicdb_agentscope; Owner: system
--

CREATE TABLE basicdb_agentscope.sys_files (
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


ALTER TABLE basicdb_agentscope.sys_files OWNER TO system;

--
-- TOC entry 245 (class 1259 OID 4483279)
-- Name: sys_i18n; Type: TABLE; Schema: basicdb_agentscope; Owner: system
--

CREATE TABLE basicdb_agentscope.sys_i18n (
    message_id character varying(255) NOT NULL,
    value text NOT NULL,
    language character varying(50) NOT NULL
);


ALTER TABLE basicdb_agentscope.sys_i18n OWNER TO system;

--
-- TOC entry 4001 (class 0 OID 0)
-- Dependencies: 245
-- Name: COLUMN sys_i18n.message_id; Type: COMMENT; Schema: basicdb_agentscope; Owner: system
--

COMMENT ON COLUMN basicdb_agentscope.sys_i18n.message_id IS '标识';


--
-- TOC entry 4002 (class 0 OID 0)
-- Dependencies: 245
-- Name: COLUMN sys_i18n.value; Type: COMMENT; Schema: basicdb_agentscope; Owner: system
--

COMMENT ON COLUMN basicdb_agentscope.sys_i18n.value IS '值';


--
-- TOC entry 4003 (class 0 OID 0)
-- Dependencies: 245
-- Name: COLUMN sys_i18n.language; Type: COMMENT; Schema: basicdb_agentscope; Owner: system
--

COMMENT ON COLUMN basicdb_agentscope.sys_i18n.language IS '语言';


--
-- TOC entry 246 (class 1259 OID 4483284)
-- Name: sys_job; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_job (
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


ALTER TABLE basicdb_agentscope.sys_job OWNER TO postgres;

--
-- TOC entry 247 (class 1259 OID 4483293)
-- Name: sys_job_job_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_job_job_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_job_job_id_seq OWNER TO postgres;

--
-- TOC entry 4005 (class 0 OID 0)
-- Dependencies: 247
-- Name: sys_job_job_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_job_job_id_seq OWNED BY basicdb_agentscope.sys_job.job_id;


--
-- TOC entry 248 (class 1259 OID 4483294)
-- Name: sys_job_log; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_job_log (
    job_log_id bigint NOT NULL,
    job_name character varying(64) NOT NULL,
    job_group character varying(64) NOT NULL,
    invoke_target character varying(500) NOT NULL,
    job_message character varying(500),
    status character varying(3) DEFAULT '0'::character varying,
    exception_info character varying(2000),
    create_time timestamp(0) without time zone
);


ALTER TABLE basicdb_agentscope.sys_job_log OWNER TO postgres;

--
-- TOC entry 249 (class 1259 OID 4483300)
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_job_log_job_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_job_log_job_log_id_seq OWNER TO postgres;

--
-- TOC entry 4008 (class 0 OID 0)
-- Dependencies: 249
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_job_log_job_log_id_seq OWNED BY basicdb_agentscope.sys_job_log.job_log_id;


--
-- TOC entry 250 (class 1259 OID 4483301)
-- Name: sys_logininfor; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_logininfor (
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


ALTER TABLE basicdb_agentscope.sys_logininfor OWNER TO postgres;

--
-- TOC entry 251 (class 1259 OID 4483307)
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_logininfor_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_logininfor_info_id_seq OWNER TO postgres;

--
-- TOC entry 4011 (class 0 OID 0)
-- Dependencies: 251
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_logininfor_info_id_seq OWNED BY basicdb_agentscope.sys_logininfor.info_id;


--
-- TOC entry 252 (class 1259 OID 4483308)
-- Name: sys_menu; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_menu (
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


ALTER TABLE basicdb_agentscope.sys_menu OWNER TO postgres;

--
-- TOC entry 4013 (class 0 OID 0)
-- Dependencies: 252
-- Name: COLUMN sys_menu.show_tags; Type: COMMENT; Schema: basicdb_agentscope; Owner: postgres
--

COMMENT ON COLUMN basicdb_agentscope.sys_menu.show_tags IS '自定义配置该页面是否展示Tags-view';


--
-- TOC entry 253 (class 1259 OID 4483320)
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_menu_menu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_menu_menu_id_seq OWNER TO postgres;

--
-- TOC entry 4015 (class 0 OID 0)
-- Dependencies: 253
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_menu_menu_id_seq OWNED BY basicdb_agentscope.sys_menu.menu_id;


--
-- TOC entry 254 (class 1259 OID 4483321)
-- Name: sys_notice; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_notice (
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


ALTER TABLE basicdb_agentscope.sys_notice OWNER TO postgres;

--
-- TOC entry 255 (class 1259 OID 4483327)
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_notice_notice_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_notice_notice_id_seq OWNER TO postgres;

--
-- TOC entry 4018 (class 0 OID 0)
-- Dependencies: 255
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_notice_notice_id_seq OWNED BY basicdb_agentscope.sys_notice.notice_id;


--
-- TOC entry 374 (class 1259 OID 4487134)
-- Name: sys_notice_read; Type: TABLE; Schema: basicdb_agentscope; Owner: system
--

CREATE TABLE basicdb_agentscope.sys_notice_read (
    read_id bigint NOT NULL,
    notice_id bigint NOT NULL,
    user_id bigint NOT NULL,
    read_time timestamp(0) without time zone
);


ALTER TABLE basicdb_agentscope.sys_notice_read OWNER TO system;

--
-- TOC entry 375 (class 1259 OID 4487137)
-- Name: sys_notice_read_read_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: system
--

CREATE SEQUENCE basicdb_agentscope.sys_notice_read_read_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_notice_read_read_id_seq OWNER TO system;

--
-- TOC entry 4020 (class 0 OID 0)
-- Dependencies: 375
-- Name: sys_notice_read_read_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: system
--

ALTER SEQUENCE basicdb_agentscope.sys_notice_read_read_id_seq OWNED BY basicdb_agentscope.sys_notice_read.read_id;


--
-- TOC entry 256 (class 1259 OID 4483328)
-- Name: sys_oper_log; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_oper_log (
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


ALTER TABLE basicdb_agentscope.sys_oper_log OWNER TO postgres;

--
-- TOC entry 257 (class 1259 OID 4483336)
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_oper_log_oper_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_oper_log_oper_id_seq OWNER TO postgres;

--
-- TOC entry 4022 (class 0 OID 0)
-- Dependencies: 257
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_oper_log_oper_id_seq OWNED BY basicdb_agentscope.sys_oper_log.oper_id;


--
-- TOC entry 258 (class 1259 OID 4483337)
-- Name: sys_post; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_post (
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


ALTER TABLE basicdb_agentscope.sys_post OWNER TO postgres;

--
-- TOC entry 259 (class 1259 OID 4483342)
-- Name: sys_post_post_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_post_post_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_post_post_id_seq OWNER TO postgres;

--
-- TOC entry 4025 (class 0 OID 0)
-- Dependencies: 259
-- Name: sys_post_post_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_post_post_id_seq OWNED BY basicdb_agentscope.sys_post.post_id;


--
-- TOC entry 260 (class 1259 OID 4483343)
-- Name: sys_role; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_role (
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


ALTER TABLE basicdb_agentscope.sys_role OWNER TO postgres;

--
-- TOC entry 261 (class 1259 OID 4483352)
-- Name: sys_role_dept; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_role_dept (
    role_id bigint NOT NULL,
    dept_id bigint NOT NULL
);


ALTER TABLE basicdb_agentscope.sys_role_dept OWNER TO postgres;

--
-- TOC entry 262 (class 1259 OID 4483355)
-- Name: sys_role_menu; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_role_menu (
    role_id bigint NOT NULL,
    menu_id bigint NOT NULL
);


ALTER TABLE basicdb_agentscope.sys_role_menu OWNER TO postgres;

--
-- TOC entry 263 (class 1259 OID 4483358)
-- Name: sys_role_role_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_role_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_role_role_id_seq OWNER TO postgres;

--
-- TOC entry 4030 (class 0 OID 0)
-- Dependencies: 263
-- Name: sys_role_role_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_role_role_id_seq OWNED BY basicdb_agentscope.sys_role.role_id;


--
-- TOC entry 264 (class 1259 OID 4483364)
-- Name: sys_user; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_user (
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


ALTER TABLE basicdb_agentscope.sys_user OWNER TO postgres;

--
-- TOC entry 265 (class 1259 OID 4483373)
-- Name: sys_user_post; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_user_post (
    user_id bigint NOT NULL,
    post_id bigint NOT NULL
);


ALTER TABLE basicdb_agentscope.sys_user_post OWNER TO postgres;

--
-- TOC entry 266 (class 1259 OID 4483376)
-- Name: sys_user_role; Type: TABLE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE TABLE basicdb_agentscope.sys_user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE basicdb_agentscope.sys_user_role OWNER TO postgres;

--
-- TOC entry 267 (class 1259 OID 4483379)
-- Name: sys_user_user_id_seq; Type: SEQUENCE; Schema: basicdb_agentscope; Owner: postgres
--

CREATE SEQUENCE basicdb_agentscope.sys_user_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE basicdb_agentscope.sys_user_user_id_seq OWNER TO postgres;

--
-- TOC entry 4035 (class 0 OID 0)
-- Dependencies: 267
-- Name: sys_user_user_id_seq; Type: SEQUENCE OWNED BY; Schema: basicdb_agentscope; Owner: postgres
--

ALTER SEQUENCE basicdb_agentscope.sys_user_user_id_seq OWNED BY basicdb_agentscope.sys_user.user_id;


--
-- TOC entry 3647 (class 2604 OID 4483380)
-- Name: gen_table table_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: system
--

ALTER TABLE ONLY basicdb_agentscope.gen_table ALTER COLUMN table_id SET DEFAULT nextval('basicdb_agentscope.gen_table_table_id_seq'::regclass);


--
-- TOC entry 3651 (class 2604 OID 4483381)
-- Name: gen_table_column column_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.gen_table_column ALTER COLUMN column_id SET DEFAULT nextval('basicdb_agentscope.gen_table_column_column_id_seq'::regclass);


--
-- TOC entry 3653 (class 2604 OID 4483382)
-- Name: sys_config config_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_config ALTER COLUMN config_id SET DEFAULT nextval('basicdb_agentscope.sys_config_config_id_seq'::regclass);


--
-- TOC entry 3655 (class 2604 OID 4483383)
-- Name: sys_dept dept_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_dept ALTER COLUMN dept_id SET DEFAULT nextval('basicdb_agentscope.sys_dept_dept_id_seq'::regclass);


--
-- TOC entry 3660 (class 2604 OID 4483384)
-- Name: sys_dict_data dict_code; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_dict_data ALTER COLUMN dict_code SET DEFAULT nextval('basicdb_agentscope.sys_dict_data_dict_code_seq'::regclass);


--
-- TOC entry 3664 (class 2604 OID 4483385)
-- Name: sys_dict_type dict_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_dict_type ALTER COLUMN dict_id SET DEFAULT nextval('basicdb_agentscope.sys_dict_type_dict_id_seq'::regclass);


--
-- TOC entry 3666 (class 2604 OID 4483386)
-- Name: sys_job job_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_job ALTER COLUMN job_id SET DEFAULT nextval('basicdb_agentscope.sys_job_job_id_seq'::regclass);


--
-- TOC entry 3671 (class 2604 OID 4483387)
-- Name: sys_job_log job_log_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_job_log ALTER COLUMN job_log_id SET DEFAULT nextval('basicdb_agentscope.sys_job_log_job_log_id_seq'::regclass);


--
-- TOC entry 3673 (class 2604 OID 4483388)
-- Name: sys_logininfor info_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_logininfor ALTER COLUMN info_id SET DEFAULT nextval('basicdb_agentscope.sys_logininfor_info_id_seq'::regclass);


--
-- TOC entry 3675 (class 2604 OID 4483389)
-- Name: sys_menu menu_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_menu ALTER COLUMN menu_id SET DEFAULT nextval('basicdb_agentscope.sys_menu_menu_id_seq'::regclass);


--
-- TOC entry 3683 (class 2604 OID 4483390)
-- Name: sys_notice notice_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_notice ALTER COLUMN notice_id SET DEFAULT nextval('basicdb_agentscope.sys_notice_notice_id_seq'::regclass);


--
-- TOC entry 3685 (class 2604 OID 4483391)
-- Name: sys_oper_log oper_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_oper_log ALTER COLUMN oper_id SET DEFAULT nextval('basicdb_agentscope.sys_oper_log_oper_id_seq'::regclass);


--
-- TOC entry 3689 (class 2604 OID 4483392)
-- Name: sys_post post_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_post ALTER COLUMN post_id SET DEFAULT nextval('basicdb_agentscope.sys_post_post_id_seq'::regclass);


--
-- TOC entry 3690 (class 2604 OID 4483393)
-- Name: sys_role role_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_role ALTER COLUMN role_id SET DEFAULT nextval('basicdb_agentscope.sys_role_role_id_seq'::regclass);


--
-- TOC entry 3695 (class 2604 OID 4483394)
-- Name: sys_user user_id; Type: DEFAULT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_user ALTER COLUMN user_id SET DEFAULT nextval('basicdb_agentscope.sys_user_user_id_seq'::regclass);


--
-- TOC entry 3915 (class 0 OID 4483163)
-- Dependencies: 218
-- Data for Name: gen_table; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: system
--

INSERT INTO basicdb_agentscope.gen_table VALUES (1, 'sys_role_dept', NULL, NULL, NULL, 'SysRoleDept', 'crud', 'com.basic.system', 'system', 'dept', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (2, 'sys_role_menu', NULL, NULL, NULL, 'SysRoleMenu', 'crud', 'com.basic.system', 'system', 'menu', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (3, 'sys_user', NULL, NULL, NULL, 'SysUser', 'crud', 'com.basic.system', 'system', 'user', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (4, 'sys_user_post', NULL, NULL, NULL, 'SysUserPost', 'crud', 'com.basic.system', 'system', 'post', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (5, 'sys_user_role', NULL, NULL, NULL, 'SysUserRole', 'crud', 'com.basic.system', 'system', 'role', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (6, 'sys_notice', NULL, NULL, NULL, 'SysNotice', 'crud', 'com.basic.system', 'system', 'notice', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (7, 'sys_menu', NULL, NULL, NULL, 'SysMenu', 'crud', 'com.basic.system', 'system', 'menu', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:14', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (8, 'sys_config', NULL, NULL, NULL, 'SysConfig', 'crud', 'com.basic.system', 'system', 'config', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (9, 'sys_dept', NULL, NULL, NULL, 'SysDept', 'crud', 'com.basic.system', 'system', 'dept', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (10, 'sys_dict_data', NULL, NULL, NULL, 'SysDictData', 'crud', 'com.basic.system', 'system', 'data', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (11, 'sys_dict_type', NULL, NULL, NULL, 'SysDictType', 'crud', 'com.basic.system', 'system', 'type', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (12, 'sys_job', NULL, NULL, NULL, 'SysJob', 'crud', 'com.basic.system', 'system', 'job', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (13, 'sys_job_log', NULL, NULL, NULL, 'SysJobLog', 'crud', 'com.basic.system', 'system', 'log', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (14, 'sys_logininfor', NULL, NULL, NULL, 'SysLogininfor', 'crud', 'com.basic.system', 'system', 'logininfor', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (15, 'sys_oper_log', NULL, NULL, NULL, 'SysOperLog', 'crud', 'com.basic.system', 'system', 'log', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (16, 'sys_post', NULL, NULL, NULL, 'SysPost', 'crud', 'com.basic.system', 'system', 'post', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (17, 'sys_role', NULL, NULL, NULL, 'SysRole', 'crud', 'com.basic.system', 'system', 'role', NULL, 'basic', '0', '/', NULL, 'admin', '2023-03-02 13:46:20', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table VALUES (18, 'sys_demo', NULL, NULL, NULL, 'SysDemo', 'crud', 'com.basic.system', 'system', 'demo', NULL, 'basic', '0', '/', NULL, 'admin', '2024-02-01 11:09:28', NULL, NULL, NULL);


--
-- TOC entry 3916 (class 0 OID 4483171)
-- Dependencies: 219
-- Data for Name: gen_table_column; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.gen_table_column VALUES (3, '2', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (4, '2', 'menu_id', NULL, 'bigint', 'Long', 'menuId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (5, '3', 'user_id', NULL, 'bigint', 'Long', 'userId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (6, '3', 'dept_id', NULL, 'bigint', 'Long', 'deptId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (7, '3', 'user_name', NULL, 'character varying', 'String', 'userName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 3, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (8, '3', 'nick_name', NULL, 'character varying', 'String', 'nickName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 4, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (9, '3', 'user_type', NULL, 'character varying', 'String', 'userType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 5, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (10, '3', 'email', NULL, 'character varying', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (11, '3', 'phonenumber', NULL, 'character varying', 'String', 'phonenumber', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (12, '3', 'sex', NULL, 'character varying', 'String', 'sex', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 8, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (13, '3', 'avatar', NULL, 'character varying', 'String', 'avatar', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (14, '3', 'password', NULL, 'character varying', 'String', 'password', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (15, '3', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 11, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (16, '3', 'del_flag', NULL, 'character varying', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (17, '3', 'login_ip', NULL, 'character varying', 'String', 'loginIp', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (18, '3', 'login_date', NULL, 'timestamp without time zone', 'String', 'loginDate', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (19, '3', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 15, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (20, '3', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 16, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (21, '3', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 17, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (22, '3', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 18, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (23, '3', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 19, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (24, '4', 'user_id', NULL, 'bigint', 'Long', 'userId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (25, '4', 'post_id', NULL, 'bigint', 'Long', 'postId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (26, '5', 'user_id', NULL, 'bigint', 'Long', 'userId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (27, '5', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (28, '6', 'notice_id', NULL, 'bigint', 'Long', 'noticeId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (29, '6', 'notice_title', NULL, 'character varying', 'String', 'noticeTitle', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (30, '6', 'notice_type', NULL, 'character varying', 'String', 'noticeType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', NULL, 3, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (31, '6', 'notice_content', NULL, 'bytea', 'String', 'noticeContent', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'editor', NULL, 4, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (32, '6', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 5, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (33, '6', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (34, '6', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (35, '6', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (36, '6', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (37, '6', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (38, '7', 'menu_id', NULL, 'bigint', 'Long', 'menuId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (39, '7', 'menu_name', NULL, 'character varying', 'String', 'menuName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (40, '7', 'parent_id', NULL, 'bigint', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 3, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (41, '7', 'order_num', NULL, 'integer', 'Long', 'orderNum', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 4, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (42, '7', 'path', NULL, 'character varying', 'String', 'path', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (43, '7', 'component', NULL, 'character varying', 'String', 'component', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (44, '7', 'query', NULL, 'character varying', 'String', 'query', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (45, '7', 'is_frame', NULL, 'character varying', 'String', 'isFrame', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (46, '7', 'is_cache', NULL, 'character varying', 'String', 'isCache', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (47, '7', 'menu_type', NULL, 'character varying', 'String', 'menuType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 10, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (48, '7', 'visible', NULL, 'character varying', 'String', 'visible', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (49, '7', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 12, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (50, '7', 'perms', NULL, 'character varying', 'String', 'perms', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (51, '7', 'icon', NULL, 'character varying', 'String', 'icon', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (52, '7', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 15, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (53, '7', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 16, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (54, '7', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 17, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (55, '7', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 18, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (56, '7', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 19, 'admin', '2023-03-02 13:46:14', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (57, '8', 'config_id', NULL, 'integer', 'Long', 'configId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (58, '8', 'config_name', NULL, 'character varying', 'String', 'configName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (59, '8', 'config_key', NULL, 'character varying', 'String', 'configKey', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (60, '8', 'config_value', NULL, 'character varying', 'String', 'configValue', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (61, '8', 'config_type', NULL, 'character varying', 'String', 'configType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (62, '8', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (63, '8', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (64, '8', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (65, '8', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (66, '8', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (67, '9', 'dept_id', NULL, 'bigint', 'Long', 'deptId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (68, '9', 'parent_id', NULL, 'bigint', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (69, '9', 'ancestors', NULL, 'character varying', 'String', 'ancestors', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (70, '9', 'dept_name', NULL, 'character varying', 'String', 'deptName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (71, '9', 'order_num', NULL, 'integer', 'Long', 'orderNum', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (72, '9', 'leader', NULL, 'character varying', 'String', 'leader', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (73, '9', 'phone', NULL, 'character varying', 'String', 'phone', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (74, '9', 'email', NULL, 'character varying', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (75, '9', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (76, '9', 'del_flag', NULL, 'character varying', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (77, '9', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (78, '9', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (79, '9', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (80, '9', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (81, '10', 'dict_code', NULL, 'bigint', 'Long', 'dictCode', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (82, '10', 'dict_sort', NULL, 'integer', 'Long', 'dictSort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (83, '10', 'dict_label', NULL, 'character varying', 'String', 'dictLabel', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (84, '10', 'dict_value', NULL, 'character varying', 'String', 'dictValue', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (85, '10', 'dict_type', NULL, 'character varying', 'String', 'dictType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (86, '10', 'css_class', NULL, 'character varying', 'String', 'cssClass', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (87, '10', 'list_class', NULL, 'character varying', 'String', 'listClass', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (88, '10', 'is_default', NULL, 'character varying', 'String', 'isDefault', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (89, '10', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (90, '10', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (91, '10', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (92, '10', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (93, '10', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (94, '10', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (95, '11', 'dict_id', NULL, 'bigint', 'Long', 'dictId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (96, '11', 'dict_name', NULL, 'character varying', 'String', 'dictName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (97, '11', 'dict_type', NULL, 'character varying', 'String', 'dictType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (98, '11', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (99, '11', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (100, '11', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (101, '11', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (102, '11', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (103, '11', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (104, '12', 'job_id', NULL, 'bigint', 'Long', 'jobId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (105, '12', 'job_name', NULL, 'character varying', 'String', 'jobName', '1', '0', '0', '1', NULL, NULL, NULL, 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (106, '12', 'job_group', NULL, 'character varying', 'String', 'jobGroup', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (107, '12', 'invoke_target', NULL, 'character varying', 'String', 'invokeTarget', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (108, '12', 'cron_expression', NULL, 'character varying', 'String', 'cronExpression', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (109, '12', 'misfire_policy', NULL, 'character varying', 'String', 'misfirePolicy', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (110, '12', 'concurrent', NULL, 'character varying', 'String', 'concurrent', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (111, '12', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (112, '12', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (113, '12', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (114, '12', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (115, '12', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (116, '12', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (117, '13', 'job_log_id', NULL, 'bigint', 'Long', 'jobLogId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (118, '13', 'job_name', NULL, 'character varying', 'String', 'jobName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (119, '13', 'job_group', NULL, 'character varying', 'String', 'jobGroup', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (120, '13', 'invoke_target', NULL, 'character varying', 'String', 'invokeTarget', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (121, '13', 'job_message', NULL, 'character varying', 'String', 'jobMessage', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (122, '13', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (123, '13', 'exception_info', NULL, 'character varying', 'String', 'exceptionInfo', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (124, '13', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (125, '14', 'info_id', NULL, 'bigint', 'Long', 'infoId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (126, '14', 'user_name', NULL, 'character varying', 'String', 'userName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (127, '14', 'ipaddr', NULL, 'character varying', 'String', 'ipaddr', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (128, '14', 'login_location', NULL, 'character varying', 'String', 'loginLocation', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (129, '14', 'browser', NULL, 'character varying', 'String', 'browser', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (130, '14', 'os', NULL, 'character varying', 'String', 'os', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (131, '14', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (132, '14', 'msg', NULL, 'character varying', 'String', 'msg', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (133, '14', 'login_time', NULL, 'timestamp without time zone', 'String', 'loginTime', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (134, '15', 'oper_id', NULL, 'bigint', 'Long', 'operId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (135, '15', 'title', NULL, 'character varying', 'String', 'title', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (136, '15', 'business_type', NULL, 'integer', 'Long', 'businessType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (137, '15', 'method', NULL, 'character varying', 'String', 'method', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (138, '15', 'request_method', NULL, 'character varying', 'String', 'requestMethod', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (139, '15', 'operator_type', NULL, 'integer', 'Long', 'operatorType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (140, '15', 'oper_name', NULL, 'character varying', 'String', 'operName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (141, '15', 'dept_name', NULL, 'character varying', 'String', 'deptName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (142, '15', 'oper_url', NULL, 'character varying', 'String', 'operUrl', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (143, '15', 'oper_ip', NULL, 'character varying', 'String', 'operIp', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (144, '15', 'oper_location', NULL, 'character varying', 'String', 'operLocation', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (145, '15', 'oper_param', NULL, 'character varying', 'String', 'operParam', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (146, '15', 'json_result', NULL, 'character varying', 'String', 'jsonResult', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (147, '15', 'status', NULL, 'integer', 'Long', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (148, '15', 'error_msg', NULL, 'character varying', 'String', 'errorMsg', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 15, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (149, '15', 'oper_time', NULL, 'timestamp without time zone', 'String', 'operTime', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 16, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (150, '16', 'post_id', NULL, 'bigint', 'Long', 'postId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (151, '16', 'post_code', NULL, 'character varying', 'String', 'postCode', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (152, '16', 'post_name', NULL, 'character varying', 'String', 'postName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (153, '16', 'post_sort', NULL, 'integer', 'Long', 'postSort', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (154, '16', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (155, '16', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (156, '16', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (157, '16', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (158, '16', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (159, '16', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (160, '17', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (161, '17', 'role_name', NULL, 'character varying', 'String', 'roleName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (162, '17', 'role_key', NULL, 'character varying', 'String', 'roleKey', '0', '0', '1', '1', '1', '1', '1', 'EQ', NULL, NULL, 3, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (163, '17', 'role_sort', NULL, 'integer', 'Long', 'roleSort', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', NULL, 4, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (164, '17', 'data_scope', NULL, 'character varying', 'String', 'dataScope', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 5, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (165, '17', 'menu_check_strictly', NULL, 'smallint', 'Long', 'menuCheckStrictly', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 6, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (166, '17', 'dept_check_strictly', NULL, 'smallint', 'Long', 'deptCheckStrictly', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 7, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (167, '17', 'status', NULL, 'character varying', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', NULL, 8, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (168, '17', 'del_flag', NULL, 'character varying', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 9, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (169, '17', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 10, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (170, '17', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 11, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (171, '17', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 12, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (172, '17', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 13, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (173, '17', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 14, 'admin', '2023-03-02 13:46:20', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (1, '1', 'role_id', NULL, 'bigint', 'Long', 'roleId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2023-03-02 13:46:14', NULL, '2024-01-16 11:33:08');
INSERT INTO basicdb_agentscope.gen_table_column VALUES (2, '1', 'dept_id', NULL, 'bigint', 'Long', 'deptId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 2, 'admin', '2023-03-02 13:46:14', NULL, '2024-01-16 11:33:08');
INSERT INTO basicdb_agentscope.gen_table_column VALUES (174, '18', 'id', NULL, 'bigint', 'Long', 'id', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', NULL, 1, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (175, '18', 'name', NULL, 'character varying', 'String', 'name', '0', '0', '0', '1', '1', '1', '1', 'LIKE', NULL, NULL, 2, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (176, '18', 'create_by', NULL, 'character varying', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 3, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (177, '18', 'create_time', NULL, 'timestamp without time zone', 'String', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', NULL, NULL, 4, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (178, '18', 'create_id', NULL, 'bigint', 'Long', 'createId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 5, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (179, '18', 'update_by', NULL, 'character varying', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 6, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (180, '18', 'update_time', NULL, 'timestamp without time zone', 'String', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', NULL, NULL, 7, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (181, '18', 'update_id', NULL, 'bigint', 'Long', 'updateId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', NULL, 8, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (182, '18', 'remark', NULL, 'character varying', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', NULL, NULL, 9, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (183, '18', 'is_del', NULL, 'character varying', 'String', 'isDel', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 10, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (184, '18', 'sex', NULL, 'character varying', 'String', 'sex', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', NULL, 11, 'admin', '2024-02-01 11:09:28', NULL, NULL);
INSERT INTO basicdb_agentscope.gen_table_column VALUES (185, '18', 'birth', NULL, 'timestamp without time zone', 'String', 'birth', '0', '0', '0', '1', '1', '1', '1', 'EQ', NULL, NULL, 12, 'admin', '2024-02-01 11:09:28', NULL, NULL);


--
-- TOC entry 3919 (class 0 OID 4483189)
-- Dependencies: 224
-- Data for Name: qrtz_blob_triggers; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3920 (class 0 OID 4483194)
-- Dependencies: 225
-- Data for Name: qrtz_calendars; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3921 (class 0 OID 4483199)
-- Dependencies: 226
-- Data for Name: qrtz_cron_triggers; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3922 (class 0 OID 4483204)
-- Dependencies: 227
-- Data for Name: qrtz_fired_triggers; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3923 (class 0 OID 4483209)
-- Dependencies: 228
-- Data for Name: qrtz_job_details; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3924 (class 0 OID 4483214)
-- Dependencies: 229
-- Data for Name: qrtz_locks; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3925 (class 0 OID 4483217)
-- Dependencies: 230
-- Data for Name: qrtz_paused_trigger_grps; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3926 (class 0 OID 4483220)
-- Dependencies: 231
-- Data for Name: qrtz_scheduler_state; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3927 (class 0 OID 4483223)
-- Dependencies: 232
-- Data for Name: qrtz_simple_triggers; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3928 (class 0 OID 4483228)
-- Dependencies: 233
-- Data for Name: qrtz_simprop_triggers; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3929 (class 0 OID 4483233)
-- Dependencies: 234
-- Data for Name: qrtz_triggers; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3930 (class 0 OID 4483238)
-- Dependencies: 235
-- Data for Name: sys_config; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_config VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO basicdb_agentscope.sys_config VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO basicdb_agentscope.sys_config VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO basicdb_agentscope.sys_config VALUES (10, '账号自助-密码有效期天数', 'sys.account.checkPassword.time', '90', 'Y', 'admin', '2024-02-21 11:21:18', 'admin', '2024-10-11 10:14:33', '对90天未修改密码的账号进行提醒');
INSERT INTO basicdb_agentscope.sys_config VALUES (4, '账号自助-验证码开关', 'sys.account.captchaOnOff', 'true', 'Y', 'admin', '2023-02-28 17:09:30', 'admin', '2024-01-31 17:09:54', '是否开启验证码功能（true开启，false关闭）');
INSERT INTO basicdb_agentscope.sys_config VALUES (7, '密码复杂度验证表达式', 'sys.password.inputPattern', '/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[`!@#$%^&*(\)-//.,;'':"{}\[\]\\|]).{8,15}$/', 'Y', 'admin', '2024-02-20 15:00:42', 'admin', '2024-02-20 15:55:26', '/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[`!@#$%^&*(\)-//.,;'':"{}\[\]\\|]).{8,15}$/');
INSERT INTO basicdb_agentscope.sys_config VALUES (8, '密码复杂度验证提示信息', 'sys.password.errorMessage', '用户密码长度必须介于 8 和 15 之间且包含大小写字母、数字及特殊字符', 'Y', 'admin', '2024-02-20 15:11:04', 'admin', '2024-02-20 16:03:43', NULL);
INSERT INTO basicdb_agentscope.sys_config VALUES (9, '账号自助-密码修改提醒', 'sys.account.checkPassword', 'true', 'Y', 'admin', '2024-02-21 10:59:19', NULL, NULL, '是否开启修改密码提醒(对长时间没有修改密码的账号)（true开启，false关闭）');
INSERT INTO basicdb_agentscope.sys_config VALUES (2, '账号管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2023-02-28 17:09:30', '', NULL, '初始化密码 123456');
INSERT INTO basicdb_agentscope.sys_config VALUES (11, '账号登录-是否开启账号锁定', 'user.password.lockOpen', 'true', 'Y', 'admin', '2024-02-21 11:23:26', NULL, NULL, '是否开启密码错误次数过多账号锁定（true开启，false关闭）');
INSERT INTO basicdb_agentscope.sys_config VALUES (12, '账号登录-密码错误次数锁定', 'user.password.maxRetryCount ', '3', 'Y', 'admin', '2024-02-21 11:25:02', NULL, NULL, '账号登录时密码最大错误次数');
INSERT INTO basicdb_agentscope.sys_config VALUES (13, '账号登录-账号锁定时间', 'user.password.lockTime', '30', 'Y', 'admin', '2024-02-21 11:26:27', NULL, NULL, '账号锁定时长（分钟）');


--
-- TOC entry 3932 (class 0 OID 4483245)
-- Dependencies: 237
-- Data for Name: sys_demo; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: system
--

INSERT INTO basicdb_agentscope.sys_demo VALUES (1750711753414205442, '李四', 'admin', '2024-01-26 10:46:23', 1, 'admin', '2024-02-19 11:34:34.706', NULL, NULL, '0', 'boy', '2024-01-09 00:00:00');


--
-- TOC entry 3933 (class 0 OID 4483248)
-- Dependencies: 238
-- Data for Name: sys_dept; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_dept VALUES (103, 101, '0,100,101', '研发部门', 1, '部门管理员', '15888888888', 'bdvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-07-10 17:08:12', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_dept VALUES (101, 100, '0,100', '佛山总公司', 1, '佛山管理员', '18888888888', 'bdvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-01-26 14:39:26', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_dept VALUES (100, 0, '0', '集团公司', 0, '管理员', '18888888888', 'dbvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-02-08 08:49:44', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_dept VALUES (102, 100, '0,100', '长沙分公司', 2, '长沙管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_dept VALUES (104, 101, '0,100,101', '市场部门', 2, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_dept VALUES (105, 101, '0,100,101', '测试部门', 3, '部门管理员', '15888888888', 'bdvr@email.com', '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-02-08 08:49:31', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_dept VALUES (106, 101, '0,100,101', '财务部门', 4, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_dept VALUES (107, 101, '0,100,101', '运维部门', 5, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_dept VALUES (108, 102, '0,100,102', '市场部门', 1, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_dept VALUES (109, 102, '0,100,102', '财务部门', 2, '部门管理员', '15888888888', 'bdvr@email.com', '0', '2', 'admin', '2023-02-28 17:09:30', '', NULL, NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_dept VALUES (111, 101, '0,100,101', '办公室', 3, NULL, NULL, NULL, '0', '0', 'xiaoxi', '2024-04-22 16:04:38', NULL, NULL, NULL, NULL, NULL);


--
-- TOC entry 3935 (class 0 OID 4483258)
-- Dependencies: 240
-- Data for Name: sys_dict_data; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_dict_data VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '性别男');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '性别女');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '性别未知');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '显示菜单');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '隐藏菜单');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '停用状态');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '停用状态');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '默认分组');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统分组');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统默认是');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统默认否');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '通知');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '公告');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '关闭状态');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (18, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '新增操作');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (19, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '修改操作');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (20, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '删除操作');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (21, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '授权操作');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (22, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '导出操作');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (23, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '导入操作');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (24, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '强退操作');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (25, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '生成操作');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (26, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '清空操作');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (27, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '正常状态');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (28, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '停用状态');
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (30, 0, '跟随系统', '0', 'sys_show_tags', NULL, 'default', 'N', '0', 'admin', '2024-06-25 11:15:44', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (31, 1, '显示', '1', 'sys_show_tags', NULL, 'default', 'N', '0', 'admin', '2024-06-25 11:16:01', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_dict_data VALUES (32, 2, '隐藏', '2', 'sys_show_tags', NULL, 'default', 'N', '0', 'admin', '2024-06-25 11:17:07', 'admin', '2024-06-25 11:17:13', NULL);


--
-- TOC entry 3937 (class 0 OID 4483267)
-- Dependencies: 242
-- Data for Name: sys_dict_type; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_dict_type VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '用户性别列表');
INSERT INTO basicdb_agentscope.sys_dict_type VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '菜单状态列表');
INSERT INTO basicdb_agentscope.sys_dict_type VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统开关列表');
INSERT INTO basicdb_agentscope.sys_dict_type VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '任务状态列表');
INSERT INTO basicdb_agentscope.sys_dict_type VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '任务分组列表');
INSERT INTO basicdb_agentscope.sys_dict_type VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '系统是否列表');
INSERT INTO basicdb_agentscope.sys_dict_type VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '通知类型列表');
INSERT INTO basicdb_agentscope.sys_dict_type VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '通知状态列表');
INSERT INTO basicdb_agentscope.sys_dict_type VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '操作类型列表');
INSERT INTO basicdb_agentscope.sys_dict_type VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '登录状态列表');
INSERT INTO basicdb_agentscope.sys_dict_type VALUES (12, '选项卡状态', 'sys_show_tags', '0', 'admin', '2024-06-25 11:14:52', 'admin', '2024-06-25 11:14:59', '自定义展示系统Tags-View组件');


--
-- TOC entry 3939 (class 0 OID 4483274)
-- Dependencies: 244
-- Data for Name: sys_files; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: system
--



--
-- TOC entry 3940 (class 0 OID 4483279)
-- Dependencies: 245
-- Data for Name: sys_i18n; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: system
--

INSERT INTO basicdb_agentscope.sys_i18n VALUES ('Assembly.buttonPosition', 'Please configure the buttonPosition property for the chart', 'en');
INSERT INTO basicdb_agentscope.sys_i18n VALUES ('Assembly.buttonPosition', '请给图表配置buttonPosition属性', 'zh_CN');
INSERT INTO basicdb_agentscope.sys_i18n VALUES ('Assembly.buttonPosition', '請給圖表配置buttonPosition屬性', 'zh_hant');
INSERT INTO basicdb_agentscope.sys_i18n VALUES ('Assembly.idFormStyle', 'Please configure the idFormStyle property for the chart for more information', 'en');
INSERT INTO basicdb_agentscope.sys_i18n VALUES ('Assembly.idFormStyle', '请给图表配置idFormStyle属性', 'zh_CN');
INSERT INTO basicdb_agentscope.sys_i18n VALUES ('Assembly.idFormStyle', '請給圖表配置idFormStyle屬性', 'zh_hant');


--
-- TOC entry 3941 (class 0 OID 4483284)
-- Dependencies: 246
-- Data for Name: sys_job; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_job VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2023-02-28 17:09:30', '', NULL, '');
INSERT INTO basicdb_agentscope.sys_job VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(''ry'')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2023-02-28 17:09:30', '', NULL, '');
INSERT INTO basicdb_agentscope.sys_job VALUES (3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(''ry'', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2023-02-28 17:09:30', '', NULL, '');


--
-- TOC entry 3943 (class 0 OID 4483294)
-- Dependencies: 248
-- Data for Name: sys_job_log; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3945 (class 0 OID 4483301)
-- Dependencies: 250
-- Data for Name: sys_logininfor; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3947 (class 0 OID 4483308)
-- Dependencies: 252
-- Data for Name: sys_menu; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_menu VALUES (3, '系统工具', 0, 3, 'tool', NULL, '', '1', '0', 'M', '0', '0', '', 'tool', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-13 16:16:59', '系统工具目录', NULL, '0', 'system tools', '系統工具');
INSERT INTO basicdb_agentscope.sys_menu VALUES (1, '系统管理', 0, 1, 'system', NULL, '', '1', '0', 'M', '0', '0', '', 'system', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-13 16:17:15', '系统管理目录', NULL, '0', 'system management', '系統管理');
INSERT INTO basicdb_agentscope.sys_menu VALUES (1013, '菜单查询', 102, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1014, '菜单新增', 102, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1015, '菜单修改', 102, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1016, '菜单删除', 102, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:37', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1024, '岗位删除', 104, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:post:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1025, '岗位导出', 104, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:post:export', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (108, '日志管理', 1, 9, 'log', '', '', '1', '0', 'M', '0', '0', '', 'log', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:02', '日志管理菜单', NULL, '0', 'log management', '日誌管理');
INSERT INTO basicdb_agentscope.sys_menu VALUES (1045, '日志导出', 501, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1001, '用户查询', 100, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:user:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', 'user query', '用戶查詢');
INSERT INTO basicdb_agentscope.sys_menu VALUES (1002, '用户新增', 100, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:user:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1003, '用户修改', 100, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:user:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1008, '角色查询', 101, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:role:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1009, '角色新增', 101, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:role:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1010, '角色修改', 101, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:role:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1011, '角色删除', 101, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:role:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:55', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1019, '部门修改', 103, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1020, '部门删除', 103, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1030, '字典导出', 105, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1058, '导入代码', 115, 2, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1057, '生成删除', 115, 3, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1059, '预览代码', 115, 4, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1060, '生成代码', 115, 5, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1047, '批量强退', 109, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:05', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1004, '用户删除', 100, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:user:remove', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1005, '用户导出', 100, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:user:export', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1006, '用户导入', 100, 6, '', '', '', '1', '0', 'F', '0', '0', 'system:user:import', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1007, '重置密码', 100, 7, '', '', '', '1', '0', 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1012, '角色导出', 101, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:role:export', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:55', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1017, '部门查询', 103, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:14', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1018, '部门新增', 103, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:14', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1021, '岗位查询', 104, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:post:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1022, '岗位新增', 104, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:post:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1023, '岗位修改', 104, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:post:edit', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:36', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1026, '字典查询', 105, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:query', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1027, '字典新增', 105, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:add', '#', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1028, '字典修改', 105, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1029, '字典删除', 105, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:43', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1031, '参数查询', 106, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1032, '参数新增', 106, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:add', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1033, '参数修改', 106, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (100, '用户管理', 1, 1, 'user', 'basic-admin/system/user/index', '', '1', '0', 'C', '0', '0', 'system:user:list', 'user', 'admin', '2023-02-28 17:09:30', 'admin', '2025-09-04 11:06:34', '用户管理菜单', NULL, '0', 'user management', '用戶管理');
INSERT INTO basicdb_agentscope.sys_menu VALUES (1034, '参数删除', 106, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1036, '公告查询', 107, 1, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1037, '公告新增', 107, 2, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:add', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1038, '公告修改', 107, 3, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1039, '公告删除', 107, 4, '#', '', '', '1', '0', 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:57', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1040, '操作查询', 500, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:09', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1041, '操作删除', 500, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:09', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1042, '日志导出', 500, 4, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:09', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1043, '登录查询', 501, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1044, '登录删除', 501, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:15', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1055, '生成查询', 115, 1, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1056, '生成修改', 115, 2, '#', '', '', '1', '0', 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:40:44', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1046, '在线查询', 109, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:05', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1049, '任务查询', 110, 1, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:12', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1050, '任务新增', 110, 2, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:12', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1035, '参数导出', 106, 5, '#', '', '', '1', '0', 'F', '0', '0', 'system:config:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:39:51', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1048, '单条强退', 109, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:05', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1051, '任务修改', 110, 3, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1052, '任务删除', 110, 4, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1053, '状态修改', 110, 5, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (1054, '任务导出', 110, 7, '#', '', '', '1', '0', 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2023-02-28 17:35:14', 'admin', '2025-08-14 10:41:13', '', NULL, '0', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_menu VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', '1', '0', 'M', '1', '1', '', 'monitor', 'admin', '2023-02-28 17:09:30', 'admin', '2026-10-16 17:07:09', '系统监控目录', NULL, '0', 'system monitoring', '系統監控');
INSERT INTO basicdb_agentscope.sys_menu VALUES (101, '角色管理', 1, 2, 'role', 'basic-admin/system/role/index', '', '1', '0', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:19:54', '角色管理菜单', NULL, '0', 'role management', '角色管理');
INSERT INTO basicdb_agentscope.sys_menu VALUES (102, '菜单管理', 1, 3, 'menu', 'basic-admin/system/menu/index', '', '1', '0', 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:18:36', '菜单管理菜单', NULL, '0', 'menu management', '菜單管理');
INSERT INTO basicdb_agentscope.sys_menu VALUES (103, '部门管理', 1, 4, 'dept', 'basic-admin/system/dept/index', '', '1', '0', 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:20:14', '部门管理菜单', NULL, '0', 'sector management', '部門管理');
INSERT INTO basicdb_agentscope.sys_menu VALUES (104, '岗位管理', 1, 5, 'post', 'basic-admin/system/post/index', '', '1', '0', 'C', '0', '0', 'system:post:list', 'post', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:35', '岗位管理菜单', NULL, '0', 'post management', '崗位管理');
INSERT INTO basicdb_agentscope.sys_menu VALUES (105, '字典管理', 1, 6, 'dict', 'basic-admin/system/dict/index', '', '1', '0', 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:42', '字典管理菜单', NULL, '0', 'dictionary management', '字典管理');
INSERT INTO basicdb_agentscope.sys_menu VALUES (107, '通知公告', 1, 8, 'notice', 'basic-admin/system/notice/index', '', '1', '0', 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:57', '通知公告菜单', NULL, '0', 'notice announcement', '通知公告');
INSERT INTO basicdb_agentscope.sys_menu VALUES (106, '参数设置', 1, 7, 'config', 'basic-admin/system/config/index', '', '1', '0', 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:39:51', '参数设置菜单', NULL, '0', 'parameter setting', '參數設置');
INSERT INTO basicdb_agentscope.sys_menu VALUES (500, '操作日志', 108, 1, 'operlog', 'basic-admin/monitor/operlog/index', '', '1', '0', 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:09', '操作日志菜单', NULL, '0', 'operation log', '操作日誌');
INSERT INTO basicdb_agentscope.sys_menu VALUES (501, '登录日志', 108, 2, 'logininfor', 'basic-admin/monitor/logininfor/index', '', '1', '0', 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:15', '登录日志菜单', NULL, '0', 'login logs', '登錄日誌');
INSERT INTO basicdb_agentscope.sys_menu VALUES (111, '数据监控', 3, 4, 'druid', 'basic-admin/monitor/druid/index', '', '1', '0', 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:59', '数据监控菜单', NULL, '0', 'data monitoring', '數據監控');
INSERT INTO basicdb_agentscope.sys_menu VALUES (109, '在线用户', 3, 5, 'online', 'basic-admin/monitor/online/index', '', '1', '0', 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:05', '在线用户菜单', NULL, '0', 'online users', '在線用戶');
INSERT INTO basicdb_agentscope.sys_menu VALUES (112, '服务监控', 3, 7, 'server', 'basic-admin/monitor/server/index', '', '1', '0', 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:21', '服务监控菜单', NULL, '0', 'service monitoring', '服務監控');
INSERT INTO basicdb_agentscope.sys_menu VALUES (113, '缓存监控', 3, 8, 'cache', 'basic-admin/monitor/cache/index', '', '1', '0', 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:27', '缓存监控菜单', NULL, '0', 'Cache monitoring', '緩存監控');
INSERT INTO basicdb_agentscope.sys_menu VALUES (110, '定时任务', 3, 6, 'job', 'basic-admin/monitor/job/index', '', '1', '0', 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:41:11', '定时任务菜单', NULL, '0', 'scheduled task', '定時任務');
INSERT INTO basicdb_agentscope.sys_menu VALUES (114, '表单构建', 3, 1, 'build', 'basic-admin/tool/build/index', '', '1', '0', 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:36', '表单构建菜单', NULL, '0', 'Form construction', '表單構建');
INSERT INTO basicdb_agentscope.sys_menu VALUES (115, '代码生成', 3, 2, 'gen', 'basic-admin/tool/gen/index', '', '1', '0', 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:44', '代码生成菜单', NULL, '0', 'code generation', '代碼生成');
INSERT INTO basicdb_agentscope.sys_menu VALUES (116, '系统接口', 3, 3, 'swagger', 'basic-admin/tool/swagger/index', '', '1', '0', 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2023-02-28 17:09:30', 'admin', '2025-08-14 10:40:54', '系统接口菜单', NULL, '0', 'system interface', '系統接口');


--
-- TOC entry 3949 (class 0 OID 4483321)
-- Dependencies: 254
-- Data for Name: sys_notice; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--



--
-- TOC entry 3963 (class 0 OID 4487134)
-- Dependencies: 374
-- Data for Name: sys_notice_read; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: system
--



--
-- TOC entry 3951 (class 0 OID 4483328)
-- Dependencies: 256
-- Data for Name: sys_oper_log; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_oper_log VALUES (379, '操作日志', 9, 'com.basic.web.controller.monitor.SysOperlogController.clean()', 'DELETE', 1, 'admin', NULL, '/monitor/operlog/clean', '127.0.0.1', '内网IP', '{}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 14:17:16');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (380, '登录日志', 9, 'com.basic.web.controller.monitor.SysLogininforController.clean()', 'DELETE', 1, 'admin', NULL, '/monitor/logininfor/clean', '127.0.0.1', '内网IP', '{}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 14:17:20');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (381, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1754778882690195458', '127.0.0.1', '内网IP', '{menuId=1754778882690195458}', '{"msg":"存在子菜单,不允许删除","code":500}', 0, NULL, '2026-10-14 14:17:33');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (382, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1064', '127.0.0.1', '内网IP', '{menuId=1064}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-14 14:17:41');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (383, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1955896250820567041', '127.0.0.1', '内网IP', '{menuId=1955896250820567041}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:06:47');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (384, '菜单管理', 2, 'com.basic.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":null,"createTime":"2023-02-28 17:09:30","updateBy":"admin","updateTime":null,"remark":null,"params":{},"menuId":2,"menuName":"系统监控","parentName":null,"parentId":0,"orderNum":2,"path":"monitor","component":null,"query":"","isFrame":"1","isCache":"0","menuType":"M","visible":"0","status":"0","perms":"","icon":"monitor","children":[]}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:06:56');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (385, '菜单管理', 2, 'com.basic.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{"searchValue":null,"createBy":null,"createTime":"2023-02-28 17:09:30","updateBy":"admin","updateTime":null,"remark":null,"params":{},"menuId":2,"menuName":"系统监控","parentName":null,"parentId":0,"orderNum":2,"path":"monitor","component":null,"query":"","isFrame":"1","isCache":"0","menuType":"M","visible":"1","status":"1","perms":"","icon":"monitor","children":[]}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:09');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (386, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753950971045343233', '127.0.0.1', '内网IP', '{menuId=1753950971045343233}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:24');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (387, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796354', '127.0.0.1', '内网IP', '{menuId=1753952621629796354}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:26');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (388, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796355', '127.0.0.1', '内网IP', '{menuId=1753952621629796355}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:30');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (389, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796356', '127.0.0.1', '内网IP', '{menuId=1753952621629796356}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:32');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (390, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796357', '127.0.0.1', '内网IP', '{menuId=1753952621629796357}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:34');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (391, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1753952621629796358', '127.0.0.1', '内网IP', '{menuId=1753952621629796358}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:37');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (392, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1061', '127.0.0.1', '内网IP', '{menuId=1061}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:39');
INSERT INTO basicdb_agentscope.sys_oper_log VALUES (393, '菜单管理', 3, 'com.basic.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1754778882690195458', '127.0.0.1', '内网IP', '{menuId=1754778882690195458}', '{"msg":"操作成功","code":200}', 0, NULL, '2026-10-16 17:07:42');


--
-- TOC entry 3953 (class 0 OID 4483337)
-- Dependencies: 258
-- Data for Name: sys_post; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_post VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2023-02-28 17:09:30', '', NULL, '');


--
-- TOC entry 3955 (class 0 OID 4483343)
-- Dependencies: 260
-- Data for Name: sys_role; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_role VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2023-02-28 17:09:30', '', NULL, '超级管理员');
INSERT INTO basicdb_agentscope.sys_role VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2023-02-28 17:09:30', 'admin', '2024-01-24 16:28:19', '普通角色');
INSERT INTO basicdb_agentscope.sys_role VALUES (4, '333', 'hgf', 0, '1', 1, 1, '0', '2', 'admin', '2024-02-06 14:00:18', NULL, NULL, NULL);
INSERT INTO basicdb_agentscope.sys_role VALUES (5, '用户1', 'usss', 3, '1', 1, 1, '0', '2', 'admin', '2024-02-06 16:52:25', NULL, NULL, NULL);


--
-- TOC entry 3956 (class 0 OID 4483352)
-- Dependencies: 261
-- Data for Name: sys_role_dept; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_role_dept VALUES (2, 100);
INSERT INTO basicdb_agentscope.sys_role_dept VALUES (2, 101);
INSERT INTO basicdb_agentscope.sys_role_dept VALUES (2, 105);


--
-- TOC entry 3957 (class 0 OID 4483355)
-- Dependencies: 262
-- Data for Name: sys_role_menu; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 105);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 100);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1001);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1002);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1003);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1004);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1005);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1006);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1007);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 101);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1008);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1009);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1010);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1011);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1012);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 102);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1013);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1014);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1015);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1016);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 103);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1017);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1018);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1019);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1020);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 104);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1021);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1022);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1023);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1024);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1025);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 1026);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 106);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 107);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 108);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 500);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 501);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 2);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 109);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 110);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 111);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 112);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 113);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 3);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 114);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 115);
INSERT INTO basicdb_agentscope.sys_role_menu VALUES (2, 116);


--
-- TOC entry 3959 (class 0 OID 4483364)
-- Dependencies: 264
-- Data for Name: sys_user; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_user VALUES (9, NULL, 'test002', '测试2', '00', NULL, NULL, '0', NULL, '$2a$10$udfBxRm0omFl7XpSmBk7/.wfsRfzP5qqc0EJ6bM0s01fgmoSiDIBq', '0', '0', '127.0.0.1', '2024-02-21 17:18:01', 'admin', '2024-02-21 17:17:44', NULL, '2024-02-21 17:18:00', NULL, NULL);
INSERT INTO basicdb_agentscope.sys_user VALUES (4, 101, 'xiaoan', '安全管理员', '00', 'xiaoan@email.com', '19999999999', '0', '/profile/avatar/2024/04/22/blob_20240422160106A001.jpeg', '$2a$10$So1TNdFpzE1dB09Z0eQs1OzFpFLMrWxbYtfTvxEFLMC/8SXTUPPEK', '0', '0', '127.0.0.1', '2024-10-11 10:13:43', 'admin', '2024-01-26 15:07:37', 'admin', '2024-10-11 10:13:42', '安全管理员', '2024-10-11 10:13:27');
INSERT INTO basicdb_agentscope.sys_user VALUES (6, NULL, '13249230230', 'AA', '00', NULL, NULL, '0', NULL, '$2a$10$.3MLA6zZE6RPDLzSx6jzseXhe9ShBXBgddJ5Z09Bp1immgkZ978JS', '0', '0', '192.168.170.21', '2024-02-06 15:51:51', 'admin', '2024-01-26 15:24:02', 'admin', '2024-02-06 15:51:50', NULL, '2024-02-21 11:49:28');
INSERT INTO basicdb_agentscope.sys_user VALUES (5, 100, 'xiaoxi', '系统管理员', '00', 'xiaoxi@email.com', '13333333333', '0', NULL, '$2a$10$FNM2ZPI/XwzY.arKiLUZtusSs9ui1X0xU3dfdcvLr.mb9eNskz22O', '0', '0', '192.168.170.21', '2024-04-24 09:12:57', 'admin', '2024-01-26 15:10:01', 'admin', '2024-04-24 09:12:56', '系统管理员', '2024-04-22 16:03:29');
INSERT INTO basicdb_agentscope.sys_user VALUES (7, NULL, '用户1', 'user1', '00', NULL, NULL, '0', NULL, '$2a$10$wfJ4zAYQ0kaZyp2XRyLO7.5bnloK3/M0HiOatf3kRWt//u2KMUNdu', '0', '2', NULL, NULL, 'admin', '2024-02-06 16:51:53', NULL, NULL, NULL, '2024-02-21 11:49:28');
INSERT INTO basicdb_agentscope.sys_user VALUES (2, 105, 'xiaoshen', '审计管理员', '00', 'xiaoshen@qq.com', '17777777777', '1', '/profile/avatar/2024/02/06/blob_20240206154427A003.jpeg', '$2a$10$MpBkNRsOzwiy54C/Vx8JoOjk6NV3Zz.jws030m1DMvguCcPP4OLJ6', '0', '0', '192.168.170.21', '2024-02-07 08:21:20', 'admin', '2023-02-28 17:09:30', 'admin', '2024-02-07 08:21:19', '审计管理员', '2024-02-21 11:49:28');
INSERT INTO basicdb_agentscope.sys_user VALUES (8, NULL, 'test001', '测试', '00', NULL, NULL, '0', NULL, '$2a$10$RAs8Dzmmt.UUD4JZvLX4Wes1Eblh0Z/OpQceqseQk78zhNvI62XvK', '0', '0', '127.0.0.1', '2025-08-14 10:43:36', 'admin', '2024-02-21 17:09:51', 'admin', '2025-08-14 10:43:36', NULL, '2025-08-14 10:43:21');
INSERT INTO basicdb_agentscope.sys_user VALUES (1, 100, 'admin', '超*管', '00', 'xiaochao@163.com', '18888888888', '1', '/profile/avatar/2024/02/06/blob_20240206154306A002.jpeg', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2026-10-14 14:10:31', 'admin', '2023-02-28 17:09:30', '', '2026-10-14 14:10:31', '管理员', '2024-02-21 11:49:28');


--
-- TOC entry 3960 (class 0 OID 4483373)
-- Dependencies: 265
-- Data for Name: sys_user_post; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_user_post VALUES (1, 1);


--
-- TOC entry 3961 (class 0 OID 4483376)
-- Dependencies: 266
-- Data for Name: sys_user_role; Type: TABLE DATA; Schema: basicdb_agentscope; Owner: postgres
--

INSERT INTO basicdb_agentscope.sys_user_role VALUES (1, 1);
INSERT INTO basicdb_agentscope.sys_user_role VALUES (4, 2);
INSERT INTO basicdb_agentscope.sys_user_role VALUES (2, 2);
INSERT INTO basicdb_agentscope.sys_user_role VALUES (5, 2);
INSERT INTO basicdb_agentscope.sys_user_role VALUES (8, 2);
INSERT INTO basicdb_agentscope.sys_user_role VALUES (9, 2);


--
-- TOC entry 4037 (class 0 OID 0)
-- Dependencies: 220
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.gen_table_column_column_id_seq', 185, true);


--
-- TOC entry 4038 (class 0 OID 0)
-- Dependencies: 221
-- Name: gen_table_table_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: system
--

SELECT pg_catalog.setval('basicdb_agentscope.gen_table_table_id_seq', 18, true);


--
-- TOC entry 4039 (class 0 OID 0)
-- Dependencies: 236
-- Name: sys_config_config_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_config_config_id_seq', 13, true);


--
-- TOC entry 4040 (class 0 OID 0)
-- Dependencies: 239
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_dept_dept_id_seq', 111, true);


--
-- TOC entry 4041 (class 0 OID 0)
-- Dependencies: 241
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_dict_data_dict_code_seq', 32, true);


--
-- TOC entry 4042 (class 0 OID 0)
-- Dependencies: 243
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_dict_type_dict_id_seq', 12, true);


--
-- TOC entry 4043 (class 0 OID 0)
-- Dependencies: 247
-- Name: sys_job_job_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_job_job_id_seq', 4, true);


--
-- TOC entry 4044 (class 0 OID 0)
-- Dependencies: 249
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_job_log_job_log_id_seq', 1, false);


--
-- TOC entry 4045 (class 0 OID 0)
-- Dependencies: 251
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_logininfor_info_id_seq', 724, true);


--
-- TOC entry 4046 (class 0 OID 0)
-- Dependencies: 253
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_menu_menu_id_seq', 1066, true);


--
-- TOC entry 4047 (class 0 OID 0)
-- Dependencies: 255
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_notice_notice_id_seq', 3, true);


--
-- TOC entry 4048 (class 0 OID 0)
-- Dependencies: 375
-- Name: sys_notice_read_read_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: system
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_notice_read_read_id_seq', 1, false);


--
-- TOC entry 4049 (class 0 OID 0)
-- Dependencies: 257
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_oper_log_oper_id_seq', 393, true);


--
-- TOC entry 4050 (class 0 OID 0)
-- Dependencies: 259
-- Name: sys_post_post_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_post_post_id_seq', 5, true);


--
-- TOC entry 4051 (class 0 OID 0)
-- Dependencies: 263
-- Name: sys_role_role_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_role_role_id_seq', 5, true);


--
-- TOC entry 4052 (class 0 OID 0)
-- Dependencies: 267
-- Name: sys_user_user_id_seq; Type: SEQUENCE SET; Schema: basicdb_agentscope; Owner: postgres
--

SELECT pg_catalog.setval('basicdb_agentscope.sys_user_user_id_seq', 9, true);


--
-- TOC entry 3701 (class 2606 OID 4483396)
-- Name: gen_table PRIMARY; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: system
--

ALTER TABLE ONLY basicdb_agentscope.gen_table
    ADD CONSTRAINT "PRIMARY" PRIMARY KEY (table_id);


--
-- TOC entry 3703 (class 2606 OID 4483398)
-- Name: gen_table_column PRIMARY00000; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.gen_table_column
    ADD CONSTRAINT "PRIMARY00000" PRIMARY KEY (column_id);


--
-- TOC entry 3705 (class 2606 OID 4483400)
-- Name: qrtz_blob_triggers PRIMARY00001; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.qrtz_blob_triggers
    ADD CONSTRAINT "PRIMARY00001" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 3707 (class 2606 OID 4483402)
-- Name: qrtz_calendars PRIMARY00002; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.qrtz_calendars
    ADD CONSTRAINT "PRIMARY00002" PRIMARY KEY (sched_name, calendar_name);


--
-- TOC entry 3709 (class 2606 OID 4483404)
-- Name: qrtz_cron_triggers PRIMARY00003; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.qrtz_cron_triggers
    ADD CONSTRAINT "PRIMARY00003" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 3711 (class 2606 OID 4483406)
-- Name: qrtz_fired_triggers PRIMARY00004; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.qrtz_fired_triggers
    ADD CONSTRAINT "PRIMARY00004" PRIMARY KEY (sched_name, entry_id);


--
-- TOC entry 3713 (class 2606 OID 4483408)
-- Name: qrtz_job_details PRIMARY00005; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.qrtz_job_details
    ADD CONSTRAINT "PRIMARY00005" PRIMARY KEY (sched_name, job_name, job_group);


--
-- TOC entry 3715 (class 2606 OID 4483410)
-- Name: qrtz_locks PRIMARY00006; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.qrtz_locks
    ADD CONSTRAINT "PRIMARY00006" PRIMARY KEY (sched_name, lock_name);


--
-- TOC entry 3717 (class 2606 OID 4483412)
-- Name: qrtz_paused_trigger_grps PRIMARY00007; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.qrtz_paused_trigger_grps
    ADD CONSTRAINT "PRIMARY00007" PRIMARY KEY (sched_name, trigger_group);


--
-- TOC entry 3719 (class 2606 OID 4483414)
-- Name: qrtz_scheduler_state PRIMARY00008; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.qrtz_scheduler_state
    ADD CONSTRAINT "PRIMARY00008" PRIMARY KEY (sched_name, instance_name);


--
-- TOC entry 3721 (class 2606 OID 4483416)
-- Name: qrtz_simple_triggers PRIMARY00009; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.qrtz_simple_triggers
    ADD CONSTRAINT "PRIMARY00009" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 3723 (class 2606 OID 4483418)
-- Name: qrtz_simprop_triggers PRIMARY00010; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.qrtz_simprop_triggers
    ADD CONSTRAINT "PRIMARY00010" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 3725 (class 2606 OID 4483420)
-- Name: qrtz_triggers PRIMARY00011; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.qrtz_triggers
    ADD CONSTRAINT "PRIMARY00011" PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 3728 (class 2606 OID 4483422)
-- Name: sys_config PRIMARY00012; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_config
    ADD CONSTRAINT "PRIMARY00012" PRIMARY KEY (config_id);


--
-- TOC entry 3732 (class 2606 OID 4483424)
-- Name: sys_dept PRIMARY00013; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_dept
    ADD CONSTRAINT "PRIMARY00013" PRIMARY KEY (dept_id);


--
-- TOC entry 3734 (class 2606 OID 4483426)
-- Name: sys_dict_data PRIMARY00014; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_dict_data
    ADD CONSTRAINT "PRIMARY00014" PRIMARY KEY (dict_code);


--
-- TOC entry 3736 (class 2606 OID 4483428)
-- Name: sys_dict_type PRIMARY00015; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_dict_type
    ADD CONSTRAINT "PRIMARY00015" PRIMARY KEY (dict_id);


--
-- TOC entry 3741 (class 2606 OID 4483430)
-- Name: sys_job PRIMARY00016; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_job
    ADD CONSTRAINT "PRIMARY00016" PRIMARY KEY (job_id, job_name, job_group);


--
-- TOC entry 3743 (class 2606 OID 4483432)
-- Name: sys_job_log PRIMARY00017; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_job_log
    ADD CONSTRAINT "PRIMARY00017" PRIMARY KEY (job_log_id);


--
-- TOC entry 3745 (class 2606 OID 4483434)
-- Name: sys_logininfor PRIMARY00018; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_logininfor
    ADD CONSTRAINT "PRIMARY00018" PRIMARY KEY (info_id);


--
-- TOC entry 3747 (class 2606 OID 4483436)
-- Name: sys_menu PRIMARY00019; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_menu
    ADD CONSTRAINT "PRIMARY00019" PRIMARY KEY (menu_id);


--
-- TOC entry 3749 (class 2606 OID 4483438)
-- Name: sys_notice PRIMARY00020; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_notice
    ADD CONSTRAINT "PRIMARY00020" PRIMARY KEY (notice_id);


--
-- TOC entry 3751 (class 2606 OID 4483440)
-- Name: sys_oper_log PRIMARY00021; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_oper_log
    ADD CONSTRAINT "PRIMARY00021" PRIMARY KEY (oper_id);


--
-- TOC entry 3753 (class 2606 OID 4483442)
-- Name: sys_post PRIMARY00022; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_post
    ADD CONSTRAINT "PRIMARY00022" PRIMARY KEY (post_id);


--
-- TOC entry 3755 (class 2606 OID 4483444)
-- Name: sys_role PRIMARY00023; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_role
    ADD CONSTRAINT "PRIMARY00023" PRIMARY KEY (role_id);


--
-- TOC entry 3757 (class 2606 OID 4483446)
-- Name: sys_role_dept PRIMARY00024; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_role_dept
    ADD CONSTRAINT "PRIMARY00024" PRIMARY KEY (role_id, dept_id);


--
-- TOC entry 3759 (class 2606 OID 4483448)
-- Name: sys_role_menu PRIMARY00025; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_role_menu
    ADD CONSTRAINT "PRIMARY00025" PRIMARY KEY (role_id, menu_id);


--
-- TOC entry 3761 (class 2606 OID 4483450)
-- Name: sys_user PRIMARY00026; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_user
    ADD CONSTRAINT "PRIMARY00026" PRIMARY KEY (user_id);


--
-- TOC entry 3763 (class 2606 OID 4483452)
-- Name: sys_user_post PRIMARY00027; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_user_post
    ADD CONSTRAINT "PRIMARY00027" PRIMARY KEY (user_id, post_id);


--
-- TOC entry 3765 (class 2606 OID 4483454)
-- Name: sys_user_role PRIMARY00028; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: postgres
--

ALTER TABLE ONLY basicdb_agentscope.sys_user_role
    ADD CONSTRAINT "PRIMARY00028" PRIMARY KEY (user_id, role_id);


--
-- TOC entry 3730 (class 2606 OID 4483458)
-- Name: sys_demo sys_demo_pkey; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: system
--

ALTER TABLE ONLY basicdb_agentscope.sys_demo
    ADD CONSTRAINT sys_demo_pkey PRIMARY KEY (id);


--
-- TOC entry 3739 (class 2606 OID 4483460)
-- Name: sys_files sys_files_pkey; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: system
--

ALTER TABLE ONLY basicdb_agentscope.sys_files
    ADD CONSTRAINT sys_files_pkey PRIMARY KEY (id);


--
-- TOC entry 3767 (class 2606 OID 4487139)
-- Name: sys_notice_read sys_notice_read_pkey; Type: CONSTRAINT; Schema: basicdb_agentscope; Owner: system
--

ALTER TABLE ONLY basicdb_agentscope.sys_notice_read
    ADD CONSTRAINT sys_notice_read_pkey PRIMARY KEY (read_id);


--
-- TOC entry 3737 (class 1259 OID 4483461)
-- Name: dict_type; Type: INDEX; Schema: basicdb_agentscope; Owner: postgres
--

CREATE UNIQUE INDEX dict_type ON basicdb_agentscope.sys_dict_type USING btree (dict_type);


--
-- TOC entry 3726 (class 1259 OID 4483462)
-- Name: sched_name; Type: INDEX; Schema: basicdb_agentscope; Owner: postgres
--

CREATE INDEX sched_name ON basicdb_agentscope.qrtz_triggers USING btree (sched_name, job_name, job_group);


--
-- TOC entry 3768 (class 1259 OID 4487140)
-- Name: uk_sys_notice_read_notice_user; Type: INDEX; Schema: basicdb_agentscope; Owner: system
--

CREATE UNIQUE INDEX uk_sys_notice_read_notice_user ON basicdb_agentscope.sys_notice_read USING btree (notice_id, user_id);


--
-- TOC entry 3970 (class 0 OID 0)
-- Dependencies: 218
-- Name: TABLE gen_table; Type: ACL; Schema: basicdb_agentscope; Owner: system
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.gen_table TO PUBLIC;


--
-- TOC entry 3971 (class 0 OID 0)
-- Dependencies: 219
-- Name: TABLE gen_table_column; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.gen_table_column TO PUBLIC;


--
-- TOC entry 3973 (class 0 OID 0)
-- Dependencies: 220
-- Name: SEQUENCE gen_table_column_column_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.gen_table_column_column_id_seq TO PUBLIC;


--
-- TOC entry 3975 (class 0 OID 0)
-- Dependencies: 221
-- Name: SEQUENCE gen_table_table_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: system
--

GRANT ALL ON SEQUENCE basicdb_agentscope.gen_table_table_id_seq TO PUBLIC;


--
-- TOC entry 3976 (class 0 OID 0)
-- Dependencies: 222
-- Name: TABLE list_column; Type: ACL; Schema: basicdb_agentscope; Owner: system
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.list_column TO PUBLIC;


--
-- TOC entry 3977 (class 0 OID 0)
-- Dependencies: 223
-- Name: TABLE list_table; Type: ACL; Schema: basicdb_agentscope; Owner: system
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.list_table TO PUBLIC;


--
-- TOC entry 3978 (class 0 OID 0)
-- Dependencies: 224
-- Name: TABLE qrtz_blob_triggers; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.qrtz_blob_triggers TO PUBLIC;


--
-- TOC entry 3979 (class 0 OID 0)
-- Dependencies: 225
-- Name: TABLE qrtz_calendars; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.qrtz_calendars TO PUBLIC;


--
-- TOC entry 3980 (class 0 OID 0)
-- Dependencies: 226
-- Name: TABLE qrtz_cron_triggers; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.qrtz_cron_triggers TO PUBLIC;


--
-- TOC entry 3981 (class 0 OID 0)
-- Dependencies: 227
-- Name: TABLE qrtz_fired_triggers; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.qrtz_fired_triggers TO PUBLIC;


--
-- TOC entry 3982 (class 0 OID 0)
-- Dependencies: 228
-- Name: TABLE qrtz_job_details; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.qrtz_job_details TO PUBLIC;


--
-- TOC entry 3983 (class 0 OID 0)
-- Dependencies: 229
-- Name: TABLE qrtz_locks; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.qrtz_locks TO PUBLIC;


--
-- TOC entry 3984 (class 0 OID 0)
-- Dependencies: 230
-- Name: TABLE qrtz_paused_trigger_grps; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.qrtz_paused_trigger_grps TO PUBLIC;


--
-- TOC entry 3985 (class 0 OID 0)
-- Dependencies: 231
-- Name: TABLE qrtz_scheduler_state; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.qrtz_scheduler_state TO PUBLIC;


--
-- TOC entry 3986 (class 0 OID 0)
-- Dependencies: 232
-- Name: TABLE qrtz_simple_triggers; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.qrtz_simple_triggers TO PUBLIC;


--
-- TOC entry 3987 (class 0 OID 0)
-- Dependencies: 233
-- Name: TABLE qrtz_simprop_triggers; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.qrtz_simprop_triggers TO PUBLIC;


--
-- TOC entry 3988 (class 0 OID 0)
-- Dependencies: 234
-- Name: TABLE qrtz_triggers; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.qrtz_triggers TO PUBLIC;


--
-- TOC entry 3989 (class 0 OID 0)
-- Dependencies: 235
-- Name: TABLE sys_config; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_config TO PUBLIC;


--
-- TOC entry 3991 (class 0 OID 0)
-- Dependencies: 236
-- Name: SEQUENCE sys_config_config_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_config_config_id_seq TO PUBLIC;


--
-- TOC entry 3992 (class 0 OID 0)
-- Dependencies: 238
-- Name: TABLE sys_dept; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_dept TO PUBLIC;


--
-- TOC entry 3994 (class 0 OID 0)
-- Dependencies: 239
-- Name: SEQUENCE sys_dept_dept_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_dept_dept_id_seq TO PUBLIC;


--
-- TOC entry 3995 (class 0 OID 0)
-- Dependencies: 240
-- Name: TABLE sys_dict_data; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_dict_data TO PUBLIC;


--
-- TOC entry 3997 (class 0 OID 0)
-- Dependencies: 241
-- Name: SEQUENCE sys_dict_data_dict_code_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_dict_data_dict_code_seq TO PUBLIC;


--
-- TOC entry 3998 (class 0 OID 0)
-- Dependencies: 242
-- Name: TABLE sys_dict_type; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_dict_type TO PUBLIC;


--
-- TOC entry 4000 (class 0 OID 0)
-- Dependencies: 243
-- Name: SEQUENCE sys_dict_type_dict_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_dict_type_dict_id_seq TO PUBLIC;


--
-- TOC entry 4004 (class 0 OID 0)
-- Dependencies: 246
-- Name: TABLE sys_job; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_job TO PUBLIC;


--
-- TOC entry 4006 (class 0 OID 0)
-- Dependencies: 247
-- Name: SEQUENCE sys_job_job_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_job_job_id_seq TO PUBLIC;


--
-- TOC entry 4007 (class 0 OID 0)
-- Dependencies: 248
-- Name: TABLE sys_job_log; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_job_log TO PUBLIC;


--
-- TOC entry 4009 (class 0 OID 0)
-- Dependencies: 249
-- Name: SEQUENCE sys_job_log_job_log_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_job_log_job_log_id_seq TO PUBLIC;


--
-- TOC entry 4010 (class 0 OID 0)
-- Dependencies: 250
-- Name: TABLE sys_logininfor; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_logininfor TO PUBLIC;


--
-- TOC entry 4012 (class 0 OID 0)
-- Dependencies: 251
-- Name: SEQUENCE sys_logininfor_info_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_logininfor_info_id_seq TO PUBLIC;


--
-- TOC entry 4014 (class 0 OID 0)
-- Dependencies: 252
-- Name: TABLE sys_menu; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_menu TO PUBLIC;


--
-- TOC entry 4016 (class 0 OID 0)
-- Dependencies: 253
-- Name: SEQUENCE sys_menu_menu_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_menu_menu_id_seq TO PUBLIC;


--
-- TOC entry 4017 (class 0 OID 0)
-- Dependencies: 254
-- Name: TABLE sys_notice; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_notice TO PUBLIC;


--
-- TOC entry 4019 (class 0 OID 0)
-- Dependencies: 255
-- Name: SEQUENCE sys_notice_notice_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_notice_notice_id_seq TO PUBLIC;


--
-- TOC entry 4021 (class 0 OID 0)
-- Dependencies: 256
-- Name: TABLE sys_oper_log; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_oper_log TO PUBLIC;


--
-- TOC entry 4023 (class 0 OID 0)
-- Dependencies: 257
-- Name: SEQUENCE sys_oper_log_oper_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_oper_log_oper_id_seq TO PUBLIC;


--
-- TOC entry 4024 (class 0 OID 0)
-- Dependencies: 258
-- Name: TABLE sys_post; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_post TO PUBLIC;


--
-- TOC entry 4026 (class 0 OID 0)
-- Dependencies: 259
-- Name: SEQUENCE sys_post_post_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_post_post_id_seq TO PUBLIC;


--
-- TOC entry 4027 (class 0 OID 0)
-- Dependencies: 260
-- Name: TABLE sys_role; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_role TO PUBLIC;


--
-- TOC entry 4028 (class 0 OID 0)
-- Dependencies: 261
-- Name: TABLE sys_role_dept; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_role_dept TO PUBLIC;


--
-- TOC entry 4029 (class 0 OID 0)
-- Dependencies: 262
-- Name: TABLE sys_role_menu; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_role_menu TO PUBLIC;


--
-- TOC entry 4031 (class 0 OID 0)
-- Dependencies: 263
-- Name: SEQUENCE sys_role_role_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_role_role_id_seq TO PUBLIC;


--
-- TOC entry 4032 (class 0 OID 0)
-- Dependencies: 264
-- Name: TABLE sys_user; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_user TO PUBLIC;


--
-- TOC entry 4033 (class 0 OID 0)
-- Dependencies: 265
-- Name: TABLE sys_user_post; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_user_post TO PUBLIC;


--
-- TOC entry 4034 (class 0 OID 0)
-- Dependencies: 266
-- Name: TABLE sys_user_role; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE basicdb_agentscope.sys_user_role TO PUBLIC;


--
-- TOC entry 4036 (class 0 OID 0)
-- Dependencies: 267
-- Name: SEQUENCE sys_user_user_id_seq; Type: ACL; Schema: basicdb_agentscope; Owner: postgres
--

GRANT ALL ON SEQUENCE basicdb_agentscope.sys_user_user_id_seq TO PUBLIC;


-- Completed on 2026-10-16 17:08:20

--
-- PostgreSQL database dump complete
--

