<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link rel="stylesheet" type="text/css"
      href="${pageContext.request.contextPath}/assets/widgets/daterangepicker/daterangepicker.css"/>
<script type="text/javascript" charset="utf-8"
        src="${pageContext.request.contextPath}/assets/widgets/daterangepicker/moment.js"></script>
<script type="text/javascript" charset="utf-8"
        src="${pageContext.request.contextPath}/assets/widgets/daterangepicker/daterangepicker.js"></script>
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

    .un-sekill-ac-body-td a {
        text-decoration: none;
    }

    .dropdown-menu.inner {
        max-width: 230px !important;
    }

</style>
<div class="part pad16L pad16T pad16B pad20R">
    <div class="clearfix">
        <div class="left">
            <button class="un-btn un-btn-default rulebtn">设置竞猜规则</button>
        </div>
    </div>
    <div class="right clearfix">
        <div class="display-in left pad0A mrg16R" style="width:150px">
            <select class="selectpicker" name="fields">
                <option value="0">--请选择--</option>
                <c:forEach items="${keyOrType}" var="keyOrType">
                    <c:set var="str" value="${fn:split(keyOrType, ',')}"/>
                    <option value="${str[0]}" data-type="${str[1]}">${str[0]}</option>
                </c:forEach>
            </select>
        </div>
        <div class="display-in left pad0A mrg16R" style="width:98px">
            <select class="selectpicker" name="status">
                <option value="0">--请选择--</option>
                <option value="=">=</option>
                <option value="!=">≠</option>
                <option value="包含">包含</option>
                <option value="不包含">不包含</option>
                <option value="为空">为空</option>
                <option value="不为空">不为空</option>
            </select>
        </div>
        <div class="left" style="width:210px">
            <div class="input-group un-input-group">
                <input type="text" class="form-control" name="name" placeholder="输入值">
                <span class="input-group-btn  searchBtn">
                               <a class="btn btn-default" href="javascript:;"><i class="icon_search"></i></a>
                              </span>
            </div>
        </div>
    </div>
</div>
<div class="pad16T">
    <table class="un-sekill-ac-list" id="table">
        <thead class="un-sekill-ac-header">
        <tr>
            <c:forEach items="${keys}" var="key">
                <td class="un-sekill-ac-header-td">${key}</td>
            </c:forEach>
        </tr>
        </thead>
        <tbody class="un-sekill-ac-body">
        <c:forEach items="${data.data}" var="m">
            <tr class="un-sekill-ac-body-tr">
                <c:forEach items="${keys}" var="key">
                    <td class="un-sekill-ac-body-td">
                        <div class="un-sekill-ac-body-center pad16L pad10R">
                                ${m[key] }
                        </div>
                    </td>
                </c:forEach>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<div class="clearfix" id="pageBar"></div>
</div>
<script>
    initPage('pageBar', '${data.totalPage}', '${data.pageNum}', 'toPage', '${data.totalCount}', '${param.s}' || 10);

    //翻页
    function toPage(p, type, s) {
        var url = _path + '/sys/mongoClient/findTableNameList?p=' + p + '&s=' + (s || '${param.s}') + '&tableName=${param.tableName}';
        window.location.href = url;
    }


</script>
