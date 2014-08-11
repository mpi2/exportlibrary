update phenodcc_media.media_file, phenodcc_media.file_extension, phenodcc_raw.SERIESMEDIAPARAMETERVALUE
set phenodcc_raw.SERIESMEDIAPARAMETERVALUE.URI = concat('http://images.mousephenotype.org/src/', cid,'/', lid,'/', gid,'/', sid,'/', pid,'/', qid,'/', media_file.id,'.', extension)
where phenodcc_raw.SERIESMEDIAPARAMETERVALUE.HJID = mid
and media_file.extension_id = file_extension.id;

update phenodcc_media.media_file, phenodcc_media.file_extension, phenodcc_raw.MEDIAPARAMETER
set phenodcc_raw.MEDIAPARAMETER.URI = concat('http://images.mousephenotype.org/src/', cid,'/', lid,'/', gid,'/', sid,'/', pid,'/', qid,'/', media_file.id,'.', extension)
where phenodcc_raw.MEDIAPARAMETER.HJID = mid
and media_file.extension_id = file_extension.id;
