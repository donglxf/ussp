/**
 * add by 谭荣巧  2018/1/22
 */
layui.define(['jquery', 'ztree', 'ht_config'], function (exports) {
    "use strict";

    var $ = layui.jquery
        , ztree = layui.ztree
        , tab = layui.tab
        , config = layui.ht_config
        , ELEM = '.layui-tab-item '
        , AllAuth = []
        , TreeGrid = function () {
        this.config = {
            view: {
                showLine: false,
                showIcon: false,
                addDiyDom: TreeGrid.prototype.addDiyDom
            },
            data: {
                simpleData: {
                    enable: true
                }
            }
        }
    };
    TreeGrid.prototype.set = function (options) {
        var that = this;
        $.extend(true, that.config, options);
        return that;
    };
    TreeGrid.prototype.addDiyDom = function (treeId, treeNode) {
        var that = this
            , spaceWidth = 15
            , liObj = $("#" + treeNode.tId)
            , aObj = $("#" + treeNode.tId + "_a")
            , switchObj = $("#" + treeNode.tId + "_switch")
            , icoObj = $("#" + treeNode.tId + "_ico")
            , spanObj = $("#" + treeNode.tId + "_span");
        aObj.attr('title', '');
        aObj.append('<div class="diy swich"></div>');
        var div = $(liObj).find('div').eq(0);
        switchObj.remove();
        spanObj.remove();
        icoObj.remove();
        div.append(switchObj);
        div.append(spanObj);
        var spaceStr = "<span style='height:1px;display: inline-block;width:" + (spaceWidth * treeNode.level) + "px'></span>";
        switchObj.before(spaceStr);
        var editStr = '';
        editStr += '<div class="diy">' + (treeNode.resContent == null ? '&nbsp;' : treeNode.resContent) + '</div>';
        var corpCat = '<div title="' + treeNode.fontIcon + '">' + treeNode.fontIcon + '</div>';
        editStr += '<div class="diy">' + (treeNode.CORP_CAT == '-' ? '&nbsp;' : corpCat) + '</div>';
        aObj.append(editStr);
    }

    /**
     * 查询数据
     */
    TreeGrid.prototype.render = function (obj, setting) {
        var that = this
            , newSetting = $.extend({}, that.config.setting, setting)
            , $tree = obj
            , treeId = $tree.attr("id");
        console.info($tree);
        //初始化树
        $.fn.zTree.init($tree, {
            async: {
                enable: true,
                url: config.basePath + "role/res/auth/load",
                dataFilter: function (treeId, parentNode, childNodes) {
                    if (!childNodes) return null;
                    for (var i = 0, l = childNodes.length; i < l; i++) {
                        childNodes[i].open = true;
                        childNodes[i].name = childNodes[i]["resNameCn"].replace(/\.n/g, '.');
                    }
                    return childNodes;
                }
            }
            , view: {
                showLine: false
                , showIcon: false
                , selectedMulti: false
                , addDiyDom: TreeGrid.prototype.addDiyDom
                , fontCss: function (treeId, treeNode) {
                    return (!!treeNode.highlight) ? {color: "#A60000", "font-weight": "bold"} : {
                        color: "#333",
                        "font-weight": "normal"
                    };
                }
            }
            , callback: {
                onClick: function (event, treeId, treeNode) {
                    //执行重载
                    // refreshTable();
                },
                onAsyncSuccess: function (event, treeId, treeNode) {
                    // var node = orgTree.getNodeByParam("level ", "0");
                    // if (node) {
                    //     //orgTree.selectNode(node);
                    // }
                }
            },
            data: {
                simpleData: {
                    enable: true
                    , idKey: "resCode"
                    , pIdKey: "resPanrent"
                }
            }
        });
        //添加表头
        var li_head = ' <li class="head"><a><div class="diy">菜单</div><div class="diy">按钮权限</div><div class="diy">TAB权限</div>';
        var rows = obj.find('li');
        if (rows.length > 0) {
            rows.eq(0).before(li_head)
        } else {
            $tree.append(li_head);
            $tree.append('<li ><div style="text-align: center;line-height: 30px;" >无符合条件数据</div></li>')
        }
    }


    //自动完成渲染
    var ht_treegrid = new TreeGrid();

    // ht_treegrid.render();


    exports('ht_treegrid', ht_treegrid);
}).link("/css/ztree/treeGrid.css");