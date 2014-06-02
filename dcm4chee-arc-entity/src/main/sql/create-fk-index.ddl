create index FK318FE31937EDB1AA on content_item (instance_fk);
create index FK318FE31970C135AA on content_item (code_fk);
create index FK318FE3199F40BC4C on content_item (name_fk);
create index FKD42DBF5037EDB1AA on file_ref (instance_fk);
create index FKD42DBF50206F5C8A on file_ref (filesystem_fk);
create index FKA2455AABE9B3E742 on filesystem (next_fk);
create index FK211694958151AFEA on instance (series_fk);
create index FK2116949540F8410A on instance (reject_code_fk);
create index FK211694954DC50E6B on instance (srcode_fk);
create index FK333EE69DC28D5C on mpps (drcode_fk);
create index FK333EE6A511AE1E on mpps (patient_fk);
create index FK8F9D3D30A511AE1E on mwl_item (patient_fk);
create index FKD0D3EB05206840B on patient (merge_fk);
create index FK8523EC95A511AE1E on patient_id (patient_fk);
create index FK8523EC959E0B30AA on patient_id (issuer_fk);
create index FK268C10558B0E8FE9 on rel_linked_patient_id (patient_id_fk);
create index FK268C1055A511AE1E on rel_linked_patient_id (patient_fk);
create index FK2EF025C1E344D73A on rel_study_pcode (pcode_fk);
create index FK2EF025C14BDB761E on rel_study_pcode (study_fk);
create index FKCA01FE77B729CB1 on series (inst_code_fk);
create index FKCA01FE774BDB761E on series (study_fk);
create index FKE38CD2D68151AFEA on series_req (series_fk);
create index FK786E2A3CF8FD7F43 on sps_station_aet (mwl_item_fk);
create index FK68B0DC9A511AE1E on study (patient_fk);
create index FKC9DB73DC37EDB1AA on verify_observer (instance_fk);
