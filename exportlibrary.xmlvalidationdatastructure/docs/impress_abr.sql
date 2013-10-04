SELECT IMPRESSPROCEDURE.PROCEDUREKEY,
    IMPRESSPARAMETER.PARAMETERKEY,
    IMPRESSPARAMETER.TYPE_,
    IMPRESSPARAMETER.UNIT,
    IMPRESSPARAMETER.VALUETYPE
    
FROM ADMIN.IMPRESSPIPELINE
JOIN ADMIN.IMPRESSPROCEDURE          ON IMPRESSPIPELINE.HJID = IMPRESSPROCEDURE.IMPRESSPROCEDURE_IMPRESSPIPE_0
JOIN ADMIN.IMPRESSPARAMETER          ON IMPRESSPROCEDURE.HJID = IMPRESSPARAMETER.IMPRESSPARAMETER_IMPRESSPROC_0
JOIN ADMIN.IMPRESSPARAMETERINCREMENT ON IMPRESSPARAMETER.HJID = IMPRESSPARAMETERINCREMENT.IMPRESSPARAMETERINCREMENT_IM_0
WHERE IMPRESSPIPELINE.PIPELINEKEY = 'IMPC_001'
    --AND IMPRESSPROCEDURE.PROCEDUREKEY = 'IMPC_ABR_001'
    --and IMPRESSPARAMETER.PARAMETERKEY = 'IMPC_ABR_005_001'