SELECT IMPRESSPARAMETER.PARAMETERNAME, IMPRESSPARAMETER.PARAMETERKEY,

        IMPRESSPARAMETEROPTION.NAME_

FROM IMPRESSPARAMETER 

join phenodcc_xmlvalidationresources.IMPRESSPARAMETEROPTION on (IMPRESSPARAMETER.HJID = IMPRESSPARAMETEROPTION.IMPRESSPARAMETEROPTION_IMPRE_0)
