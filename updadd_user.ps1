$user = $args[0]
$pwd = $args[1]
$secret = ConvertTo-SecureString -AsPlainText $pwd -Force

if (Get-ADUser -Filter "sAMAccountName -eq '$user'") {
    Write-Host -NoNewline "Update user $user, pwd $pwd"
    Set-ADAccountPassword -Identity $user -Reset -NewPassword $secret
    Set-ADUser -Identity $user -Enabled $True
} else {
    Write-Host -NoNewline "Create user $user, pwd $pwd"
    New-ADUser -name $user -SamaccountName $user -AccountPassword $secret -Enabled $True
}
