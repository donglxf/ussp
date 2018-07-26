layui.use(['form', 'table'], function() {
	var table = layui.table;
	var form = layui.form;
	var $ = layui.jquery;
	var loadPageSuccess = function() {
		// 公司信息
		table.render({
			id : 'boaInCompany',
			elem : '#boaInCompany',
			url : layui.ht_config.basePath + 'company/getCompanyList'
			// , where: {
			// query: {
			// businessOrgCode: "DEV1"
			// }
			// }5
			,
			limit : 15,
			limits : [ 5, 10, 15, 20, 30, 40, 50 ],
			page : true,
			height : 'full-200',
			cols : [ [ {
				type : 'numbers',
				event : 'rowClick'
			}, {
				field : 'companyName',
				width : 110,
				title : '公司名称',
				event : 'rowClick'
			}, {
				field : 'companyAddress',
				title : '公司地址',
				event : 'rowClick'
			}, {
				field : 'companyAddressContract',
				title : '分公司地址(合同)',
				event : 'rowClick'
			}, {
				field : 'companyNameContract',
				title : '分公司名称(合同)',
				event : 'rowClick'
			}, {
				field : 'companyPhone',
				title : '电话',
				event : 'rowClick'
			}, {
				field : 'companyPhoneContract',
				title : '电话(合同)',
				event : 'rowClick'
			}, {
				field : 'companyPledgeEmailContract',
				title : '抵押权人电子邮件',
				event : 'rowClick'
			}, {
				field : 'businessPhone',
				width : 100,
				title : '客服电话',
				event : 'rowClick'
			}, {
				field : 'companyPledgeEmailContract',
				title : '登录ID',
				event : 'rowClick'
			}
			// , {field: 'updateOperator', width: 100, title: '更新人'}
			// , {field: 'lastModifiedDatetime', width: 150, title:
			// '更新时间',event: 'rowClick'}
			, {
				fixed : 'right',
				width : 230,
				title : '操作',
				align : 'center',
				toolbar : '#boaInCompany_datatable_bar'
			} ] ]
		});

		// 监听工具条
		table.on('tool(filter_boaInCompany)', function(obj) {
			var data = obj.data;
			if (obj.event === 'detail') {
				// layer.msg('ID：' + data.id + ' 的查看操作');
				
			} else if (obj.event === 'del') {
				layer.confirm('真的删除行么', function(index) {
					obj.del();
					layer.close(index);
				});
			} else if (obj.event === 'edit') {
				// 表单初始赋值
				openWindos("add_companyInfo_div");
				editCompany(data,"add_companyInfo_div");
			//	layer.alert('编辑行：<br>' + JSON.stringify(data));
			}
		});
	}
	// 加载引入页面
	$("loadpage").each(function() {
		$(this).load("/html/company/" + $(this).attr("src"), function() {
			loadPageSuccess();
		});
	});

	// 表单赋值
	var editCompany = function(data,divId) {
		$("#"+divId+"undefined input").each(function() {
			//alert($(this).attr("name"));
			var name = $(this).attr("name");
			var thisInput = $(this);
			$.each(data, function(key, val) {  // key是序号:1,2,...., val是遍历值.
				//alert(key+"***"+val);
				if(key==name){
					thisInput.val(val);
				}
			  });
			
		});
		//form.render();
	}
	
	var type = $(this).data('type');
	//打开弹出层
	var openWindos = function(divId){
		layer.open({
			type : 1,
			area : [ '800px', '600px' ],
			maxmin : true,
			shadeClose : true,
			offset : type // 具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
			,
			id : divId + type // 防止重复弹出
			,
			content : $("#"+divId).html(),
			btn : '关闭',
			btnAlign : 'c' // 按钮居中
			,
			shade : 0 // 不显示遮罩
			,
			yes : function() {
				layer.closeAll();
			}
		});
	}
});
