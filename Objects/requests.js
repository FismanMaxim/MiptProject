let activeUser = new User();
let activeCompany = new Company();
let companyList = new Map();
company1 = new Company(0, "МТС", 500, 101, 76, 3500, 76, []);
company2 = new Company(1, "Яндекс", 500, 116, 230, 3500, 230, []);
company3 = new Company(2, "Сбер", 500, 24, 3, 3500, 3, []);
company4 = new Company(3, "1С", 500, 55, 25, 3500, 25, []);
company5 = new Company(4, "Физтех", 500, 90, 777, 3500, 777, []);
companyList.set("МТС", company1);
companyList.set("Яндекс", company2);
companyList.set("Сбер", company3);
companyList.set("1С", company4);
companyList.set("Физтех", company5);

const url = 'file:///Users/dmitry/Desktop/ProjectUI/MainPage/index.html';

function authorization() {
	let inputLogin = document.querySelector(".lInputName").value;
	let inputPassword = document.querySelector(".lInputPass").value;
	let isCompany = document.querySelector(".lToggle").value;
	
	try {
		inputLogin = document.querySelector(".lInputName").value;
		inputPassword = document.querySelector(".lInputPass").value;
		isCompany = document.querySelector(".lToggle").value;
	} catch(error) {
		console.error(error);
		return;
	}
	
	if (isCompany) {
		const data = new URLSearchParams();
		data.append('companyName', inputLogin);
		data.append('companyPass', inputPassword);
		
		if (!chechData(data)){
			selectInput()
			return;
		}
		
		fetch(url + '/companies?' + data, {
			method: 'GET', 
			headers: {
				'Content-Type': 'application/json; charset=utf-8'
			}
		})
			.then(response => response.json())
			.then(data => aciveCompany = data)
			.catch(error => console.error(error));
			
	} else {
		const data = new URLSearchParams();
		data.append('userName', inputLogin);
		data.append('userPass', inputPassword);
		
		if (!chechData(data)){
			selectInput()
			return;
		}
		
		fetch(url + '/users?' + data, {
			method: 'GET', 
			headers: {
				'Content-Type': 'application/json;charset=utf-8'
			}
		})
			.then(response => response.json())
			.then(data => activeUser = data)
			.catch(error => console.error(error));
	}
}

function registration() {
	let inputLogin;
	let inputPassword;
	let isCompany;
	
	try {
		inputLogin = document.querySelector(".rInputName").value;
		inputPassword = document.querySelector(".rInputPass").value;
		isCompany = document.querySelector(".rToggle").value;
	} catch(error) {
		console.error(error);
		return;
	}
	
	if (isCompany) {
		const data = new URLSearchParams();
		data.append('companyName', inputLogin);
		data.append('companyPass', inputPassword);
		
		if (!chechData(data)){
			selectInput()
			return;
		}
		
		fetch(url + '/companies', {
			method: 'PUT', 
			headers: {
				'Content-Type': 'application/json;charset=utf-8'
			},
			body: data
		})
			.then(response => response.json())
			.then(data => aciveCompany = data)
			.catch(error => console.error(error));
	} else {
		const data = new URLSearchParams();
		data.append('userName', inputLogin);
		data.append('userPass', inputPassword);
		
		if (!chechData(data)){
			selectInput()
			return;
		}
		
		fetch(url + '/users', {
			method: 'PUT', 
			headers: {
				'Content-Type': 'application/json;charset=utf-8'
			},
			body: data
		})
			.then(response => activeUser.json())
			.then(data => aciveCompany = data)
			.catch(error => console.error(error));
	}
}

function getCompanies() {
	fetch(url + '/companies', {
		method: 'GET', 
		headers: {
			'Content-Type': 'application/json;charset=utf-8'
		}
	})
		.then(response => activeUser.json())
		.then(data => companyList = data)
		.catch(error => console.error(error));
}

function buyShares() {
	let delta = 0;
	let companyId;
	
	try {
		delta = document.querySelector(".lInputSharesDelta").value;
		companyId = companyList.get(chosenCompany.companyName).id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	const data = new URLSearchParams();
	data.append('companyId', companyId);
	data.append('delta', delta);
	
	if (!chechData(data)){
		selectInput()
		return;
	}
		
	let userId = activeUser.id;
	fetch(url+"/users/" + userId +"/shares", {
		method:"POST", 
		headers: {
			'Content-Type': 'application/json;charset=utf-8'
		},
		body: data
	})
}

function selShares() {
	let delta;
	let companyId;
	
	try {
		delta = -document.querySelector(".lInputSharesDelta").value;
		companyId = companyList.get(chosenCompany.companyName).id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	const data = new URLSearchParams();
	data.append('companyId', companyId);
	data.append('delta', delta);
	
	if (!chechData(data)){
		selectInput()
		return;
	}
	
	let userId = activeUser.id;
	fetch(url+"/users/" + userId +"/shares", {
		method:"POST", 
		headers: {
			'Content-Type': 'application/json;charset=utf-8'
		},
		body: data
	})
}

function chechData(data) {
	let flag = true;
	data.forEach((value, key) => {
		if (!value) {
			flag = false;
		}
	});
	return flag;
}
function selectInput() {
	document.documentElement.style.setProperty('--input-color', '#FF0000');
	setTimeout(removeInputSelections, 1000);
}

function removeInputSelections() {
	document.documentElement.style.setProperty('--input-color', '#8e8e8e');
}
