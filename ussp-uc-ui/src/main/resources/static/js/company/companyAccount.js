layui.use('table', function(){
  var table = layui.table;
  
  // 公司信息
  table.render({
      id: 'boaInCompanyAccount'
      , elem: '#boaInCompanyAccount'
      , url: layui.ht_config.basePath + 'company/getCompanyAccountList'
      // , where: {
      //     query: {
      //         businessOrgCode: "DEV1"
      //     }
      // }5
      , limit : 15
      , limits :[5, 10,15, 20, 30, 40, 50]
      , page: true
      , height: 'full-200'
      , cols: [[
          {type: 'numbers',event: 'rowClick'}
          , {field: 'accountName', width: 110, title: '账户名',event: 'rowClick'}
          , {field: 'bankCard',   title: '银行卡号',event: 'rowClick'}
          , {field: 'bankName',   title: '银行名称',event: 'rowClick'}
          , {field: 'openName',   title: '开户行名',event: 'rowClick'}
          , {field: 'companyCode',   title: '分公司编码',event: 'rowClick'}
          , {field: 'createOperator',   title: '创建人',event: 'rowClick'}
          , {field: 'createdDatetime',   title: '创建时间',event: 'rowClick'}
         // , {field: 'updateOperator', width: 100, title: '更新人'}
         // , {field: 'lastModifiedDatetime', width: 150, title: '更新时间',event: 'rowClick'}
           , {fixed: 'right', width: 230, title: '操作', align: 'center', toolbar: '#boaInCompanyAccount_datatable_bar'}
      ]]
  });
  
  //监听工具条
  table.on('tool(filter_boaInCompanyAccount)', function(obj){
    var data = obj.data;
    if(obj.event === 'detail'){
      layer.msg('ID：'+ data.id + ' 的查看操作');
    } else if(obj.event === 'del'){
      layer.confirm('真的删除行么', function(index){
        obj.del();
        layer.close(index);
      });
    } else if(obj.event === 'edit'){
      layer.alert('编辑行：<br>'+ JSON.stringify(data))
    }
  });
  
});