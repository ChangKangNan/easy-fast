select name,numbers from test_user
left join tax_rate on  tax_rate.id =test_user.id
where  test_user.name = #{name} and tax_rate.product_name=#{prod}
 and tax_rate.id=#{id}