<#macro bind path>
    <#if htmlEscape?exists>
        <#global status = springMacroRequestContext.getBindStatus(path, htmlEscape)>
    <#else>
        <#global status = springMacroRequestContext.getBindStatus(path)>
    </#if>
    <#-- global a temporary value, forcing a string representation for any
    kind of variable. This temp value is only used in this macro lib -->
    <#if status.value?exists && status.value?is_boolean>
        <#global stringStatusValue=status.value?string>
    <#else>
        <#global stringStatusValue=status.value?default("")>
    </#if>
</#macro>
