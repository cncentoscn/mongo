<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    .fgf {
        color: #ddd;
    }

    .opt a {
        display: inline-block;
        padding: 0 5px;
    }

    .opt .links {
        margin-top: 3px;
    }
</style>
<div class="part pad16L pad16T pad16B pad20R">
    <div class="clearfix">
        <div class="left">

        </div>
        <div class="right clearfix">

        </div>
    </div>
    <div class="un-panel mrg16T">
        <table class="un-table" id="table">
            <thead>
            <tr>
                <th>数据表名</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${listNames}" var="m">
                <tr>
                    <td>
                        <div style="width: 260px;overflow: hidden;text-overflow: ellipsis;">${m}</div>
                    </td>
                    <td class="opt">
                        <div class="links">
                            <a href="${pageContext.request.contextPath}/sys/mongoClient/findTableNameList?tableName=${m}"
                               class="btn-link font-blue">查询表数据</a><span class="fgf"></span>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <div id="page" class="clearfix"></div>
    </div>
</div>
<script>

</script>
